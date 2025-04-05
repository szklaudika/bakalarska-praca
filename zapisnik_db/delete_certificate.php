<?php
header("Content-Type: application/json");

// Načítajte pripojovacie údaje z JAWSDB_URL, ak je nastavená; inak použite lokálne nastavenia.
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

// Vytvorenie pripojenia k databáze
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["error" => "Connection failed: " . $conn->connect_error]);
    exit();
}

// Skontrolujte, či je parameter 'id' poskytnutý v URL
if (!isset($_GET['id'])) {
    http_response_code(400);
    echo json_encode(["error" => "Missing certificate id"]);
    exit();
}

$id = intval($_GET['id']);  // Konverzia na celé číslo pre bezpečnosť

// Pripravte a vykonajte SQL dotaz na vymazanie certifikátu podľa ID
$stmt = $conn->prepare("DELETE FROM certificates WHERE id = ?");
if (!$stmt) {
    http_response_code(500);
    echo json_encode(["error" => "Failed to prepare SQL statement: " . $conn->error]);
    exit();
}
$stmt->bind_param("i", $id);

if ($stmt->execute()) {
    if ($stmt->affected_rows > 0) {
        echo json_encode(["success" => true, "message" => "Certificate deleted successfully"]);
    } else {
        echo json_encode(["success" => true, "message" => "Certificate not found, nothing to delete"]);
    }
} else {
    http_response_code(500);
    echo json_encode(["error" => "Failed to delete certificate: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>
