
CREATE TYPE supply.delivery_method AS ENUM (
  'DELIVERY',
  'PICKUP'
);

ALTER TABLE supply.requisition_content
ADD COLUMN delivery_method delivery_method;

UPDATE supply.requisition_content
SET delivery_method = 'DELIVERY'::delivery_method;

ALTER TABLE supply.requisition_content
ALTER COLUMN delivery_method SET NOT NULL;

