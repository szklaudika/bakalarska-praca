CREATE TABLE flights (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date VARCHAR(50),
    departure_place VARCHAR(100),
    departure_time VARCHAR(50),
    arrival_place VARCHAR(100),
    arrival_time VARCHAR(50),
    aircraft_model VARCHAR(100),
    registration VARCHAR(50),
    single_pilot_time INT,
    multi_pilot_time INT,
    total_flight_time INT,
    pilot_name VARCHAR(100),
    single_pilot TINYINT(1),
    landings_day INT,
    landings_night INT,
    night_time INT,
    ifr_time INT,
    pic_time INT,
    copilot_time INT,
    dual_time INT,
    instructor_time INT,
    fstd_date VARCHAR(50),
    fstd_type VARCHAR(100),
    fstd_total_time INT,
    remarks TEXT
);


