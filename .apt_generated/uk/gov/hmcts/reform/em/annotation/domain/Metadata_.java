package uk.gov.hmcts.reform.em.annotation.domain;

import java.util.UUID;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Metadata.class)
public abstract class Metadata_ {

	public static volatile SingularAttribute<Metadata, String> createdBy;
	public static volatile SingularAttribute<Metadata, Integer> rotationAngle;
	public static volatile SingularAttribute<Metadata, UUID> documentId;
	public static volatile SingularAttribute<Metadata, Long> id;

	public static final String CREATED_BY = "createdBy";
	public static final String ROTATION_ANGLE = "rotationAngle";
	public static final String DOCUMENT_ID = "documentId";
	public static final String ID = "id";

}

