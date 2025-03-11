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
    // Set required fields (assume they must be provided)
    $date = !empty($data['date']) ? $data['date'] : null;
    $departurePlace = !empty($data['departurePlace']) ? $data['departurePlace'] : null;
    $departureTime = !empty($data['departureTime']) ? $data['departureTime'] : null;
    $arrivalPlace = !empty($data['arrivalPlace']) ? $data['arrivalPlace'] : null;
    $arrivalTime = !empty($data['arrivalTime']) ? $data['arrivalTime'] : null;
    $aircraftModel = !empty($data['aircraftModel']) ? $data['aircraftModel'] : null;
    $registration = !empty($data['registration']) ? $data['registration'] : null;
    
    // Required numeric field (assumed provided)
    $singlePilotTime = isset($data['singlePilotTime']) ? (int)$data['singlePilotTime'] : null;
    $totalFlightTime = isset($data['totalFlightTime']) ? (int)$data['totalFlightTime'] : null;
    
    $pilotName = !empty($data['pilotName']) ? $data['pilotName'] : null;
    
    // Optional fields – if not provided or empty, use null
    $multiPilotTime = (isset($data['multiPilotTime']) && $data['multiPilotTime'] !== "") ? (int)$data['multiPilotTime'] : null;
    $landingsDay = (isset($data['landingsDay']) && $data['landingsDay'] !== "") ? (int)$data['landingsDay'] : null;
    $landingsNight = (isset($data['landingsNight']) && $data['landingsNight'] !== "") ? (int)$data['landingsNight'] : null;
    $nightTime = (isset($data['nightTime']) && $data['nightTime'] !== "") ? (int)$data['nightTime'] : null;
    $ifrTime = (isset($data['ifrTime']) && $data['ifrTime'] !== "") ? (int)$data['ifrTime'] : null;
    $picTime = (isset($data['picTime']) && $data['picTime'] !== "") ? (int)$data['picTime'] : null;
    $copilotTime = (isset($data['copilotTime']) && $data['copilotTime'] !== "") ? (int)$data['copilotTime'] : null;
    $dualTime = (isset($data['dualTime']) && $data['dualTime'] !== "") ? (int)$data['dualTime'] : null;
    $instructorTime = (isset($data['instructorTime']) && $data['instructorTime'] !== "") ? (int)$data['instructorTime'] : null;
    
    // Optional FSTD fields: if empty or 'null', assign null
    $fstdDate = !empty($data['fstdDate']) ? $data['fstdDate'] : null;
    $fstdType = (isset($data['fstdType']) && $data['fstdType'] !== "null" && $data['fstdType'] !== "") ? $data['fstdType'] : null;
    $fstdTotalTime = (isset($data['fstdTotalTime']) && $data['fstdTotalTime'] !== "") ? (int)$data['fstdTotalTime'] : null;
    
    $remarks = isset($data['remarks']) ? $data['remarks'] : null;
    
    // Single pilot flag as boolean (stored as int)
    $singlePilot = isset($data['singlePilot']) ? (int)$data['singlePilot'] : null;
    
    // Prepare SQL statement (24 columns)
    $stmt = $conn->prepare("INSERT INTO flights 
        (date, departure_place, departure_time, arrival_place, arrival_time, aircraft_model, registration, 
         single_pilot_time, multi_pilot_time, total_flight_time, pilot_name, single_pilot, landings_day, landings_night, 
         night_time, ifr_time, pic_time, copilot_time, dual_time, instructor_time, fstd_date, fstd_type, fstd_total_time, remarks) 
         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    
    // Bind parameters with matching types:
    // s: date, departure_place, departure_time, arrival_place, arrival_time, aircraft_model, registration
    // i: single_pilot_time, multi_pilot_time, total_flight_time
    // s: pilot_name
    // i: single_pilot, landings_day, landings_night, night_time, ifr_time, pic_time, copilot_time, dual_time, instructor_time
    // s: fstd_date, fstd_type
    // i: fstd_total_time
    // s: remarks
    $stmt->bind_param(
        "sssssssiiisiiiiiiiiissis",
        $date,
        $departurePlace,
        $departureTime,
        $arrivalPlace,
        $arrivalTime,
        $aircraftModel,
        $registration,
        $singlePilotTime,
        $multiPilotTime,
        $totalFlightTime,
        $pilotName,
        $singlePilot,
        $landingsDay,
        $landingsNight,
        $nightTime,
        $ifrTime,
        $picTime,
        $copilotTime,
        $dualTime,
        $instructorTime,
        $fstdDate,
        $fstdType,
        $fstdTotalTime,
        $remarks
    );

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
