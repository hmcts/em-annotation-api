CREATE INDEX CONCURRENTLY IF NOT EXISTS bookmark_documentid
ON public.bookmark (document_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS annotationset_documentid
ON public.annotation_set (document_id);