package uk.gov.hmcts.reform.em.annotation.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Tag.class)
public abstract class Tag_ {

	public static volatile SingularAttribute<Tag, String> color;
	public static volatile SingularAttribute<Tag, String> createdBy;
	public static volatile SingularAttribute<Tag, String> name;
	public static volatile SingularAttribute<Tag, String> label;

	public static final String COLOR = "color";
	public static final String CREATED_BY = "createdBy";
	public static final String NAME = "name";
	public static final String LABEL = "label";

}

