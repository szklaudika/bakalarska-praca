<?php
header("Content-Type: application/json");

// Database connection details
$servername = "localhost";
$username = "root";         // Replace with your DB username
$password = "";             // Replace with your DB password
$dbname = "zapisnik_db";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(array("error" => "Connection failed: " . $conn->connect_error));
    exit();
}

$sql = "SELECT id, date, departure_place, departure_time, arrival_place, arrival_time, aircraft_model, registration, 
         single_pilot_time, multi_pilot_time, total_flight_time, pilot_name, single_pilot, landings_day, landings_night, 
         night_time, ifr_time, pic_time, copilot_time, dual_time, instructor_time, fstd_date, fstd_type, fstd_total_time, remarks FROM flights";
$result = $conn->query($sql);

$flights = array();
if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $flights[] = $row;
    }
}
echo json_encode($flights);
$conn->close();
?>
