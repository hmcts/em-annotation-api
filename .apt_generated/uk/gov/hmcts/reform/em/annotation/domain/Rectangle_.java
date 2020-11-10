package uk.gov.hmcts.reform.em.annotation.domain;

import java.util.UUID;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Rectangle.class)
public abstract class Rectangle_ extends uk.gov.hmcts.reform.em.annotation.domain.AbstractAuditingEntity_ {

	public static volatile SingularAttribute<Rectangle, Annotation> annotation;
	public static volatile SingularAttribute<Rectangle, Double> x;
	public static volatile SingularAttribute<Rectangle, Double> width;
	public static volatile SingularAttribute<Rectangle, Double> y;
	public static volatile SingularAttribute<Rectangle, UUID> id;
	public static volatile SingularAttribute<Rectangle, Double> height;

	public static final String ANNOTATION = "annotation";
	public static final String X = "x";
	public static final String WIDTH = "width";
	public static final String Y = "y";
	public static final String ID = "id";
	public static final String HEIGHT = "height";

}

