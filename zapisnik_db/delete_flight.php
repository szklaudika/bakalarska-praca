<?php
header("Content-Type: application/json");

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
    echo json_encode(["error" => "Connection failed: " . $conn->connect_error]);
    exit();
}

if (!isset($_GET['id'])) {
    http_response_code(400);
    echo json_encode(["error" => "Missing flight id"]);
    exit();
}

$id = intval($_GET['id']);

$stmt = $conn->prepare("DELETE FROM flights WHERE id = ?");
if (!$stmt) {
    http_response_code(500);
    echo json_encode(["error" => "Failed to prepare SQL statement: " . $conn->error]);
    exit();
}
$stmt->bind_param("i", $id);

if ($stmt->execute()) {
    if ($stmt->affected_rows > 0) {
        echo json_encode(["success" => true, "message" => "Flight deleted successfully"]);
    } else {
        echo json_encode(["success" => true, "message" => "Flight not found, nothing to delete"]);
    }
} else {
    http_response_code(500);
    echo json_encode(["error" => "Failed to delete flight: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>
