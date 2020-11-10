package uk.gov.hmcts.reform.em.annotation.domain;

import java.time.Instant;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AbstractAuditingEntity.class)
public abstract class AbstractAuditingEntity_ {

	public static volatile SingularAttribute<AbstractAuditingEntity, IdamDetails> createdByDetails;
	public static volatile SingularAttribute<AbstractAuditingEntity, Instant> createdDate;
	public static volatile SingularAttribute<AbstractAuditingEntity, String> createdBy;
	public static volatile SingularAttribute<AbstractAuditingEntity, Instant> lastModifiedDate;
	public static volatile SingularAttribute<AbstractAuditingEntity, String> lastModifiedBy;
	public static volatile SingularAttribute<AbstractAuditingEntity, IdamDetails> lastModifiedByDetails;

	public static final String CREATED_BY_DETAILS = "createdByDetails";
	public static final String CREATED_DATE = "createdDate";
	public static final String CREATED_BY = "createdBy";
	public static final String LAST_MODIFIED_DATE = "lastModifiedDate";
	public static final String LAST_MODIFIED_BY = "lastModifiedBy";
	public static final String LAST_MODIFIED_BY_DETAILS = "lastModifiedByDetails";

}

