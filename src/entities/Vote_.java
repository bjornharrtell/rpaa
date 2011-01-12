package entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2011-01-09T02:35:54.441+0100")
@StaticMetamodel(Vote.class)
public class Vote_ {
	public static volatile SingularAttribute<Vote, Integer> id;
	public static volatile SingularAttribute<Vote, String> principal;
	public static volatile SingularAttribute<Vote, Subject> subject;
}
