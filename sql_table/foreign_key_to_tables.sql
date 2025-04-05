ALTER TABLE certificates
  ADD COLUMN IF NOT EXISTS user_id INT;

ALTER TABLE certificates 
  ADD CONSTRAINT fk_certificates_user_id FOREIGN KEY (user_id) REFERENCES users(id);
  
  ALTER TABLE flights
  ADD COLUMN IF NOT EXISTS user_id INT;

ALTER TABLE flights 
  ADD CONSTRAINT fk_flights_user_id FOREIGN KEY (user_id) REFERENCES users(id);

