CREATE INDEX CONCURRENTLY IF NOT EXISTS bookmark_documentid_createdby
ON bookmark (document_id, created_by);