<?php
$servername = "localhost";
$username = "root"; // Change this if needed
$password = ""; // Default is empty for XAMPP
$dbname = "zapisnik_db";

// Connect to the database
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Connection failed: " . $conn->connect_error]));
}

// Get JSON data from the request
$data = json_decode(file_get_contents("php://input"), true);

if (!empty($data)) {
    // Prepare SQL statement
    $stmt = $conn->prepare("INSERT INTO flights 
        (date, departure_place, departure_time, arrival_place, arrival_time, aircraft_model, registration, 
        single_pilot_time, multi_pilot_time, total_flight_time, pilot_name, landings, night_time, ifr_time, 
        pic_time, copilot_time, dual_time, instructor_time, fstd_date, fstd_type, fstd_total_time, remarks) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

    // Handle fstd_type as NULL
    $fstd_type = $data['fstdType'] !== 'null' ? $data['fstdType'] : NULL;

    // Bind parameters with matching types
    $stmt->bind_param("sssssssiiisiiiiiiissis",
        $data['date'], $data['departurePlace'], $data['departureTime'], $data['arrivalPlace'], $data['arrivalTime'], 
        $data['aircraftModel'], $data['registration'], $data['singlePilotTime'], $data['multiPilotTime'], 
        $data['totalFlightTime'], $data['pilotName'], $data['landings'], $data['nightTime'], $data['ifrTime'], 
        $data['picTime'], $data['copilotTime'], $data['dualTime'], $data['instructorTime'], $data['fstdDate'], 
        $data['fstdType'], $data['fstdTotalTime'], $data['remarks']);

    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Flight added successfully"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Failed to add flight"]);
    }

    $stmt->close();
} else {
    echo json_encode(["status" => "error", "message" => "No data received"]);
}

$conn->close();
?>
