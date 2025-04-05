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
    // Retrieve flight_id from the data
    $flightId = isset($data['id']) ? (int)$data['id'] : null;

    // Ensure we have the flight ID
    if ($flightId === null) {
        echo json_encode(["status" => "error", "message" => "Flight ID is required"]);
        exit;
    }

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
    $multiPilotTime = isset($data['multiPilotTime']) && $data['multiPilotTime'] !== "" ? (int)$data['multiPilotTime'] : null;
    $landingsDay = isset($data['landingsDay']) && $data['landingsDay'] !== "" ? (int)$data['landingsDay'] : null;
    $landingsNight = isset($data['landingsNight']) && $data['landingsNight'] !== "" ? (int)$data['landingsNight'] : null;
    $nightTime = isset($data['nightTime']) && $data['nightTime'] !== "" ? (int)$data['nightTime'] : null;
    $ifrTime = isset($data['ifrTime']) && $data['ifrTime'] !== "" ? (int)$data['ifrTime'] : null;
    $picTime = isset($data['picTime']) && $data['picTime'] !== "" ? (int)$data['picTime'] : null;
    $copilotTime = isset($data['copilotTime']) && $data['copilotTime'] !== "" ? (int)$data['copilotTime'] : null;
    $dualTime = isset($data['dualTime']) && $data['dualTime'] !== "" ? (int)$data['dualTime'] : null;
    $instructorTime = isset($data['instructorTime']) && $data['instructorTime'] !== "" ? (int)$data['instructorTime'] : null;
    
    // Optional FSTD fields
    $fstdDate = !empty($data['fstdDate']) ? $data['fstdDate'] : null;
    $fstdType = isset($data['fstdType']) && $data['fstdType'] !== "null" && $data['fstdType'] !== "" ? $data['fstdType'] : null;
    $fstdTotalTime = isset($data['fstdTotalTime']) && $data['fstdTotalTime'] !== "" ? (int)$data['fstdTotalTime'] : null;
    
    $remarks = isset($data['remarks']) ? $data['remarks'] : null;
    
    // Single pilot flag as boolean (stored as int)
    $singlePilot = isset($data['singlePilot']) ? (int)$data['singlePilot'] : null;

    // Prepare SQL statement to update the flight
    $stmt = $conn->prepare("UPDATE flights SET 
        date = ?, 
        departure_place = ?, 
        departure_time = ?, 
        arrival_place = ?, 
        arrival_time = ?, 
        aircraft_model = ?, 
        registration = ?, 
        single_pilot_time = ?, 
        multi_pilot_time = ?, 
        total_flight_time = ?, 
        pilot_name = ?, 
        single_pilot = ?, 
        landings_day = ?, 
        landings_night = ?, 
        night_time = ?, 
        ifr_time = ?, 
        pic_time = ?, 
        copilot_time = ?, 
        dual_time = ?, 
        instructor_time = ?, 
        fstd_date = ?, 
        fstd_type = ?, 
        fstd_total_time = ?, 
        remarks = ? 
        WHERE id = ?");

    // Bind parameters using the correct format string:
    // Format: "sssssssiiisiiiiiiiiissisi"
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
        $flightId
    );

    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Flight updated successfully"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Failed to update flight"]);
    }

    $stmt->close();
} else {
    echo json_encode(["status" => "error", "message" => "No data received"]);
}

$conn->close();
?>
