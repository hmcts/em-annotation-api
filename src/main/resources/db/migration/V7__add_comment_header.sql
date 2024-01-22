ALTER TABLE public.annotation
ADD COLUMN IF NOT EXISTS case_id varchar(20),
ADD COLUMN IF NOT EXISTS jurisdiction varchar(20),
ADD COLUMN IF NOT EXISTS comment_header varchar(255);
