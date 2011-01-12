package entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2011-01-09T02:35:54.317+0100")
@StaticMetamodel(Subject.class)
public class Subject_ {
	public static volatile SingularAttribute<Subject, Integer> id;
	public static volatile SingularAttribute<Subject, String> name;
	public static volatile SingularAttribute<Subject, String> principal;
	public static volatile SingularAttribute<Subject, Category> category;
	public static volatile ListAttribute<Subject, Vote> votes;
}
