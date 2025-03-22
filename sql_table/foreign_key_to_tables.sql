ALTER TABLE certificates 
  ADD CONSTRAINT fk_certificates_user FOREIGN KEY (user_id) REFERENCES users(id);
  
ALTER TABLE flights 
  ADD CONSTRAINT fk_flights_user FOREIGN KEY (user_id) REFERENCES users(id);
