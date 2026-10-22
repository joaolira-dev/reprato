CREATE TABLE stock (
  offer_id BIGINT PRIMARY KEY,
  available INTEGER NOT NULL CHECK (available >= 0)
);
CREATE TABLE stock_reservations (
  order_id UUID PRIMARY KEY,
  offer_id BIGINT NOT NULL REFERENCES stock(offer_id),
  quantity INTEGER NOT NULL CHECK (quantity > 0),
  status VARCHAR(20) NOT NULL CHECK (status IN ('RESERVED','RELEASED'))
);
INSERT INTO stock (offer_id, available) VALUES (1,4),(2,3),(3,2),(4,5);
