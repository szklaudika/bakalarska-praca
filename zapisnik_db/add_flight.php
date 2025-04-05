<?php
header("Content-Type: application/json");

// Načítajte pripojovacie údaje z environmentálnej premennej JAWSDB_URL (Heroku)
// Ak nie je nastavená, použijeme lokálne údaje.
$dbUrl = getenv('JAWSDB_URL');
if ($dbUrl) {
    $dbparts = parse_url($dbUrl);
    $servername = $dbparts['host'];
    $username = $dbparts['user'];
    $password = $dbparts['pass'];
    $dbname = ltrim($dbparts['path'], '/');
} else {
    $servername = "localhost";
    $username = "root";
    $password = "";
    $dbname = "zapisnik_db";
}

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Connection failed: " . $conn->connect_error]);
    exit();
}

// Get JSON data from the request
$data = json_decode(file_get_contents("php://input"), true);

if (!empty($data)) {
    // Retrieve user_id from the data
    $userId = isset($data['userId']) ? (int)$data['userId'] : null;

    // Set required fields
    $date = !empty($data['date']) ? $data['date'] : null;
    $departurePlace = !empty($data['departurePlace']) ? $data['departurePlace'] : null;
    $departureTime = !empty($data['departureTime']) ? $data['departureTime'] : null;
    $arrivalPlace = !empty($data['arrivalPlace']) ? $data['arrivalPlace'] : null;
    $arrivalTime = !empty($data['arrivalTime']) ? $data['arrivalTime'] : null;
    $aircraftModel = !empty($data['aircraftModel']) ? $data['aircraftModel'] : null;
    $registration = !empty($data['registration']) ? $data['registration'] : null;
    
    // Required numeric fields
    $singlePilotTime = isset($data['singlePilotTime']) ? (int)$data['singlePilotTime'] : null;
    $totalFlightTime = isset($data['totalFlightTime']) ? (int)$data['totalFlightTime'] : null;
    
    $pilotName = !empty($data['pilotName']) ? $data['pilotName'] : null;
    
    // Optional fields
    $multiPilotTime = (isset($data['multiPilotTime']) && $data['multiPilotTime'] !== "") ? (int)$data['multiPilotTime'] : null;
    $landingsDay = (isset($data['landingsDay']) && $data['landingsDay'] !== "") ? (int)$data['landingsDay'] : null;
    $landingsNight = (isset($data['landingsNight']) && $data['landingsNight'] !== "") ? (int)$data['landingsNight'] : null;
    $nightTime = (isset($data['nightTime']) && $data['nightTime'] !== "") ? (int)$data['nightTime'] : null;
    $ifrTime = (isset($data['ifrTime']) && $data['ifrTime'] !== "") ? (int)$data['ifrTime'] : null;
    $picTime = (isset($data['picTime']) && $data['picTime'] !== "") ? (int)$data['picTime'] : null;
    $copilotTime = (isset($data['copilotTime']) && $data['copilotTime'] !== "") ? (int)$data['copilotTime'] : null;
    $dualTime = (isset($data['dualTime']) && $data['dualTime'] !== "") ? (int)$data['dualTime'] : null;
    $instructorTime = (isset($data['instructorTime']) && $data['instructorTime'] !== "") ? (int)$data['instructorTime'] : null;
    
    // Optional FSTD fields
    $fstdDate = !empty($data['fstdDate']) ? $data['fstdDate'] : null;
    $fstdType = (isset($data['fstdType']) && $data['fstdType'] !== "null" && $data['fstdType'] !== "") ? $data['fstdType'] : null;
    $fstdTotalTime = (isset($data['fstdTotalTime']) && $data['fstdTotalTime'] !== "") ? (int)$data['fstdTotalTime'] : null;
    
    $remarks = isset($data['remarks']) ? $data['remarks'] : null;
    
    // Single pilot flag as boolean (stored as int)
    $singlePilot = isset($data['singlePilot']) ? (int)$data['singlePilot'] : null;
    
    // Prepare SQL statement to insert into flights (25 columns)
    $stmt = $conn->prepare("INSERT INTO flights 
        (date, departure_place, departure_time, arrival_place, arrival_time, aircraft_model, registration, 
         single_pilot_time, multi_pilot_time, total_flight_time, pilot_name, single_pilot, landings_day, landings_night, 
         night_time, ifr_time, pic_time, copilot_time, dual_time, instructor_time, fstd_date, fstd_type, fstd_total_time, remarks, user_id) 
         VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    
    $stmt->bind_param(
        "sssssssiiisiiiiiiiiissisi",
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
        $remarks,
        $userId
    );

    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Flight added successfully"]);
    } else {
        http_response_code(500);
        echo json_encode(["status" => "error", "message" => "Failed to add flight: " . $stmt->error]);
    }
    
    $stmt->close();
} else {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "No data received"]);
}

$conn->close();
?>
