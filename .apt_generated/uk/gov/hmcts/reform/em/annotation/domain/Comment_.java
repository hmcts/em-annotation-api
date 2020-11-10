package uk.gov.hmcts.reform.em.annotation.domain;

import java.util.UUID;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Comment.class)
public abstract class Comment_ extends uk.gov.hmcts.reform.em.annotation.domain.AbstractAuditingEntity_ {

	public static volatile SingularAttribute<Comment, Annotation> annotation;
	public static volatile SingularAttribute<Comment, UUID> id;
	public static volatile SingularAttribute<Comment, String> content;

	public static final String ANNOTATION = "annotation";
	public static final String ID = "id";
	public static final String CONTENT = "content";

}

