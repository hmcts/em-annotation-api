package uk.gov.hmcts.reform.em.annotation.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(IdamDetails.class)
public abstract class IdamDetails_ {

	public static volatile SingularAttribute<IdamDetails, String> forename;
	public static volatile SingularAttribute<IdamDetails, String> surname;
	public static volatile SingularAttribute<IdamDetails, String> id;
	public static volatile SingularAttribute<IdamDetails, String> email;

	public static final String FORENAME = "forename";
	public static final String SURNAME = "surname";
	public static final String ID = "id";
	public static final String EMAIL = "email";

}

