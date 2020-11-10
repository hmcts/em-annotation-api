package uk.gov.hmcts.reform.em.annotation.domain;

import java.util.UUID;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Annotation.class)
public abstract class Annotation_ extends uk.gov.hmcts.reform.em.annotation.domain.AbstractAuditingEntity_ {

	public static volatile SetAttribute<Annotation, Comment> comments;
	public static volatile SingularAttribute<Annotation, String> color;
	public static volatile SetAttribute<Annotation, Rectangle> rectangles;
	public static volatile SingularAttribute<Annotation, String> annotationType;
	public static volatile SingularAttribute<Annotation, AnnotationSet> annotationSet;
	public static volatile SingularAttribute<Annotation, UUID> id;
	public static volatile SingularAttribute<Annotation, Integer> page;
	public static volatile SetAttribute<Annotation, Tag> tags;

	public static final String COMMENTS = "comments";
	public static final String COLOR = "color";
	public static final String RECTANGLES = "rectangles";
	public static final String ANNOTATION_TYPE = "annotationType";
	public static final String ANNOTATION_SET = "annotationSet";
	public static final String ID = "id";
	public static final String PAGE = "page";
	public static final String TAGS = "tags";

}

