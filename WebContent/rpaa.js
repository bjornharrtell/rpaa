rpaa = {
	version : '0.9.1'
};

rpaa.categoriesStore = null;

rpaa.preload = function() {
	rpaa.categoriesStore = new Ext.data.JsonStore({
		root : 'rows',
		fields : [ 'id', 'name' ],
		autoLoad : true,
		proxy : new Ext.data.HttpProxy({
			method : 'GET',
			url : 'jaxrs/categories'
		})
	});

	var onLoad = function() {
		rpaa.init();
		rpaa.categoriesStore.un('load', onLoad);
	};

	rpaa.categoriesStore.on('load', onLoad);
};

rpaa.init = function() {

	Ext.QuickTips.init();

	var selectedRecord = null;
	var user = null;

	var formatStatusString = function(user) {
		var status = 'Välkommen ' + user.principal + ', ';

		if (user.votesLeft === 0) {
			status += ' tack för att du röstat :)';
		} else {
			status += ' du har ' + user.votesLeft + ' röster kvar att lägga...';
		}

		return status;
	};

	var store = new Ext.data.GroupingStore({
		reader : new Ext.data.JsonReader({
			root : 'rows',
			fields : [ 'id', {
				name : 'name',
				type : 'string'
			}, 'category', {
				name : 'principal',
				type : 'string'
			}, {
				name : 'votes',
				type : 'int'
			} ]
		}),
		groupField : 'category',
		autoLoad : true,
		proxy : new Ext.data.HttpProxy({
			method : 'GET',
			url : 'jaxrs/subjects'
		})
	});

	var colModel = new Ext.grid.ColumnModel({
		defaults : {
			width : 120,
			sortable : false,
			menuDisabled : true
		},
		columns : [ {
			header : 'Namn',
			dataIndex : 'name',
			editable : true,
			editor : Ext.form.TextField
		}, {
			header : 'Ansvarig',
			dataIndex : 'principal',
			editable : false,
			width : 24
		}, {
			header : 'Kategori',
			dataIndex : 'category',
			hidden : true,
			editable : false,
			width : 34,
			renderer : function(value) {
				if (value === null || value === '') {
					return "Ingen";
				}
				return rpaa.categoriesStore.getById(value).get("name");
			}
		}, {
			header : 'Röster',
			dataIndex : 'votes',
			editable : false,
			width : 24
		} ]
	});

	var subjectAddWindowOnSubmit = function() {
		var window = this.ownerCt.ownerCt.ownerCt;
		var form = this.ownerCt.ownerCt.getForm();
		var values = form.getValues();
		var category = this.ownerCt.ownerCt.categoryCombo.getValue();

		Ext.Ajax.request({
			url : 'jaxrs/subjects',
			jsonData : values,
			success : function(response) {
				var id = parseInt(response.responseText);

				var subjectData = Ext.applyIf({
					id : id,
					category : category,
					principal : user.principal,
					votes : 0
				}, values);
				var record = new store.recordType(subjectData);
				store.add(record);

				Ext.Ajax.request({
					url : 'jaxrs/categories/' + category + '/subjects',
					jsonData : subjectData,
					success : function(response) {
						form.reset();
						window.hide();
					}
				});
			}
		});
	};

	var subjectAddWindow = new Ext.Window({
		title : 'Nytt ämne',
		width : 300,
		height : 160,
		border : false,
		constrain : true,
		modal : true,
		closeAction : 'hide',
		layout : 'fit',
		items : {
			xtype : 'form',
			labelWidth : 75,
			frame : true,
			url : 'jaxrs/subjects',
			items : [ {
				name : 'name',
				xtype : 'textfield',
				fieldLabel : 'Namn',
				anchor : '95%'
			}, {
				ref : 'categoryCombo',
				hiddenName : 'category',
				// submitValue : false,
				xtype : 'combo',
				fieldLabel : 'Kategori',
				anchor : '95%',
				store : rpaa.categoriesStore,
				triggerAction : 'all',
				valueField : 'id',
				displayField : 'name',
				typeAhead : true,
				forceSelection : true,
				emptyText : 'Okänd'
			} ],
			buttons : [ {
				text : 'Spara',
				handler : subjectAddWindowOnSubmit
			} ]
		}
	});

	var categoryAddWindowOnSubmit = function() {
		var window = this.ownerCt.ownerCt.ownerCt;
		var form = this.ownerCt.ownerCt.getForm();
		var values = form.getValues();

		Ext.Ajax.request({
			url : 'jaxrs/categories',
			jsonData : values,
			success : function(response) {
				var id = parseInt(response.responseText);
				var record = new rpaa.categoriesStore.recordType(values, id);
				rpaa.categoriesStore.add(record);
				form.reset();
				window.hide();
			}
		});
	};

	var categoryAddWindow = new Ext.Window({
		title : 'Ny kategori',
		width : 300,
		height : 130,
		border : false,
		constrain : true,
		modal : true,
		closeAction : 'hide',
		layout : 'fit',
		items : {
			xtype : 'form',
			labelWidth : 75,
			frame : true,
			url : 'jaxrs/categories',
			items : [ {
				name : 'name',
				xtype : 'textfield',
				fieldLabel : 'Namn',
				anchor : '95%'
			} ],
			buttons : [ {
				text : 'Spara',
				handler : categoryAddWindowOnSubmit
			} ]
		}
	});

	var onRowselect = function(sm, rowIndex, r) {
		var topToolbar = this.grid.getTopToolbar();
		if (user.votesLeft !== 0) {
			topToolbar.voteButton.enable();
		}
		topToolbar.principalSetButton.enable();
		topToolbar.deleteButton.enable();
		selectedRecord = r;

		Ext.Ajax.request({
			url : 'jaxrs/subjects/' + selectedRecord.id,
			headers : {
				'Accept' : 'text/plain'
			},
			success : function(response) {
				viewport.description.setValue(response.responseText);
			}
		});
	};

	var onVote = function() {
		Ext.Ajax.request({
			url : 'jaxrs/subjects/' + selectedRecord.id + '/votes',
			method : 'POST',
			success : function() {
				var votes = selectedRecord.get("votes");
				selectedRecord.set("votes", votes + 1);

				user.votesLeft = user.votesLeft - 1;

				if (user.votesLeft === 0) {
					var topToolbar = viewport.rpaa.getTopToolbar();
					topToolbar.voteButton.disable();
					var bbar = viewport.vbox.getBottomToolbar();
					bbar.statusLabel.setText(formatStatusString(user));
				}
			}
		});
	};

	var onDelete = function() {
		store.remove(selectedRecord);

		Ext.Ajax.request({
			url : 'jaxrs/subjects/' + selectedRecord.get('id'),
			method : 'DELETE'
		});

		if (selectedRecord.get("votes") > 0) {
			Ext.Ajax.request({
				url : 'jaxrs/users/current',
				success : function(response) {
					user = Ext.decode(response.responseText);
					var bbar = viewport.vbox.getBottomToolbar();
					bbar.statusLabel.setText(formatStatusString(user));
				}
			});
		}

		var sm = viewport.grid.getSelectionModel();
		sm.selectRow(sm.last, false);
	};

	var onAfteredit = function(e) {
		var record = e.record;

		Ext.Ajax.request({
			url : 'jaxrs/subjects/' + record.id,
			method : 'PUT',
			jsonData : record.data,
			success : function() {

			}
		});
	};

	var onSave = function(field, newValue, oldValue) {
		if (!selectedRecord) {
			return;
		}

		var value = viewport.description.getValue();

		Ext.Ajax.request({
			url : 'jaxrs/subjects/' + selectedRecord.id,
			method : 'PUT',
			params : value,
			headers : {
				'Content-Type' : 'text/plain'
			}
		});
	};

	var viewport = new Ext.Viewport({
		layout : 'fit',

		items : {
			ref : 'vbox',
			// layout : 'vbox',
			layout : {
				type : 'vbox',
				align : 'stretch'
			},
			items : [ {
				ref : '../grid',
				xtype : 'editorgrid',
				flex : 1,
				border : false,
				store : store,
				colModel : colModel,
				viewConfig : new Ext.grid.GroupingView({
					forceFit : true,
					markDirty : false
				}),
				sm : new Ext.grid.RowSelectionModel({
					singleSelect : true,
					listeners : {
						rowselect : onRowselect
					}
				}),
				listeners : {
					'afteredit' : onAfteredit
				},
				tbar : [ '-', {
					iconCls : 'rpaa-action-subjectadd',
					tooltip : 'Lägg till ämne',
					handler : function() {
						subjectAddWindow.show();
					}
				}, {
					iconCls : 'rpaa-action-categoryadd',
					tooltip : 'Lägg till kategori',
					handler : function() {
						categoryAddWindow.show();
					}
				}, '-', {
					ref : 'voteButton',
					iconCls : 'rpaa-action-vote',
					tooltip : 'Rösta på valt ämne',
					disabled : true,
					handler : onVote
				}, {
					ref : 'principalSetButton',
					iconCls : 'rpaa-action-principalset',
					tooltip : 'Ta över ansvaret för valt ämne',
					disabled : true,
					handler : function() {
						selectedRecord.set('principal', user.principal);
					}
				}, {
					ref : 'deleteButton',
					iconCls : 'rpaa-action-delete',
					tooltip : 'Ta bort valt ämne',
					disabled : true,
					handler : onDelete
				}, '->', {
					text : 'RPÄA ' + rpaa.version
				} ]
			}, {
				title : 'Beskrivning',
				xtype : 'form',
				layout : 'fit',
				border : false,
				height : 120,
				tools : [ {
					id : 'save',
					handler : onSave
				} ],
				items : [ {
					ref : '../../description',
					xtype : 'textarea'
				} ]
			} ],
			bbar : [ '-', {
				ref : 'statusLabel',
				xtype : 'label',
				text : ''
			} ]
		}
	});

	Ext.Ajax.request({
		url : 'jaxrs/users/current',
		success : function(response) {
			user = Ext.decode(response.responseText);
			var bbar = viewport.vbox.getBottomToolbar();
			bbar.statusLabel.setText(formatStatusString(user));
		}
	});
};

Ext.onReady(rpaa.preload);
