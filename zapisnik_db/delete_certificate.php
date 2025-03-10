<?php
header("Content-Type: application/json");

// Konfigurácia pripojenia k databáze
$servername = "localhost";  // Change if needed
$username = "root";         // Your database username
$password = "";             // Your database password
$dbname = "zapisnik_db";  

// Vytvorenie spojenia
$conn = new mysqli($servername, $username, $password, $dbname);

// Kontrola spojenia
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(array("error" => "Connection failed: " . $conn->connect_error));
    exit();
}

// Získanie id certifikátu z query stringu
if (!isset($_GET['id'])) {
    http_response_code(400);
    echo json_encode(array("error" => "Missing certificate id"));
    exit();
}

$id = intval($_GET['id']);

// Príprava a vykonanie SQL dotazu na vymazanie certifikátu
$stmt = $conn->prepare("DELETE FROM certificates WHERE id = ?");
$stmt->bind_param("i", $id);

if ($stmt->execute()) {
    if ($stmt->affected_rows > 0) {
        echo json_encode(array("success" => true, "message" => "Certificate deleted successfully"));
    } else {
        // Ak sa neovplyvnili žiadne riadky, certifikát pravdepodobne neexistoval
        echo json_encode(array("success" => true, "message" => "Certificate not found, nothing to delete"));
    }
} else {
    http_response_code(500);
    echo json_encode(array("error" => "Failed to delete certificate"));
}

$stmt->close();
$conn->close();
?>
