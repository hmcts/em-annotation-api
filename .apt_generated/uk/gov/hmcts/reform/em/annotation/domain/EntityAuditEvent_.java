package uk.gov.hmcts.reform.em.annotation.domain;

import java.time.Instant;
import java.util.UUID;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(EntityAuditEvent.class)
public abstract class EntityAuditEvent_ {

	public static volatile SingularAttribute<EntityAuditEvent, String> entityValue;
	public static volatile SingularAttribute<EntityAuditEvent, String> entityType;
	public static volatile SingularAttribute<EntityAuditEvent, Instant> modifiedDate;
	public static volatile SingularAttribute<EntityAuditEvent, String> action;
	public static volatile SingularAttribute<EntityAuditEvent, UUID> entityId;
	public static volatile SingularAttribute<EntityAuditEvent, String> modifiedBy;
	public static volatile SingularAttribute<EntityAuditEvent, UUID> id;
	public static volatile SingularAttribute<EntityAuditEvent, Integer> commitVersion;

	public static final String ENTITY_VALUE = "entityValue";
	public static final String ENTITY_TYPE = "entityType";
	public static final String MODIFIED_DATE = "modifiedDate";
	public static final String ACTION = "action";
	public static final String ENTITY_ID = "entityId";
	public static final String MODIFIED_BY = "modifiedBy";
	public static final String ID = "id";
	public static final String COMMIT_VERSION = "commitVersion";

}

