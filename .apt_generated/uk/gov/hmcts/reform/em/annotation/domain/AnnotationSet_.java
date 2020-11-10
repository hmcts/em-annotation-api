package uk.gov.hmcts.reform.em.annotation.domain;

import java.util.UUID;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AnnotationSet.class)
public abstract class AnnotationSet_ extends uk.gov.hmcts.reform.em.annotation.domain.AbstractAuditingEntity_ {

	public static volatile SetAttribute<AnnotationSet, Annotation> annotations;
	public static volatile SingularAttribute<AnnotationSet, String> documentId;
	public static volatile SingularAttribute<AnnotationSet, UUID> id;

	public static final String ANNOTATIONS = "annotations";
	public static final String DOCUMENT_ID = "documentId";
	public static final String ID = "id";

}

