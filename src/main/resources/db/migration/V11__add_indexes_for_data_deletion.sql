CREATE INDEX CONCURRENTLY IF NOT EXISTS metadata_documentid
ON public.metadata (document_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS annotation_annotationsetid
ON public.annotation (annotation_set_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS rectangle_annotationid
ON public.rectangle (annotation_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS comment_annotationid
ON public.comment (annotation_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS annotationtags_annotationid
ON public.annotation_tags (annotation_id);
