package uk.gov.hmcts.reform.em.annotation.domain;

import java.util.UUID;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Bookmark.class)
public abstract class Bookmark_ {

	public static volatile SingularAttribute<Bookmark, Double> xCoordinate;
	public static volatile SingularAttribute<Bookmark, UUID> parent;
	public static volatile SingularAttribute<Bookmark, Integer> pageNumber;
	public static volatile SingularAttribute<Bookmark, Double> yCoordinate;
	public static volatile SingularAttribute<Bookmark, UUID> previous;
	public static volatile SingularAttribute<Bookmark, String> createdBy;
	public static volatile SingularAttribute<Bookmark, String> name;
	public static volatile SingularAttribute<Bookmark, UUID> documentId;
	public static volatile SingularAttribute<Bookmark, UUID> id;

	public static final String X_COORDINATE = "xCoordinate";
	public static final String PARENT = "parent";
	public static final String PAGE_NUMBER = "pageNumber";
	public static final String Y_COORDINATE = "yCoordinate";
	public static final String PREVIOUS = "previous";
	public static final String CREATED_BY = "createdBy";
	public static final String NAME = "name";
	public static final String DOCUMENT_ID = "documentId";
	public static final String ID = "id";

}

