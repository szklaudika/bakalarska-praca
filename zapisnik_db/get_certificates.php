<?php
header("Content-Type: application/json");

// Použite environmentálnu premennú pre pripojenie k databáze, ak je nastavená (Heroku), inak použite lokálne údaje.
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
    die(json_encode(["error" => "Database connection failed: " . $conn->connect_error]));
}

// Ak je poskytnutý parameter 'user_id', načítame len certifikáty pre daného používateľa.
// Inak vrátime všetky certifikáty.
if (isset($_GET['user_id']) && !empty($_GET['user_id'])) {
    $user_id = intval($_GET['user_id']); // Konverzia na celé číslo pre bezpečnosť
    $sql = "SELECT * FROM certificates WHERE user_id = ?";
    $stmt = $conn->prepare($sql);
    if ($stmt) {
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        $result = $stmt->get_result();
        $certificates = [];
        while ($row = $result->fetch_assoc()) {
            $certificates[] = $row;
        }
        echo json_encode($certificates);
        $stmt->close();
    } else {
        echo json_encode(["error" => "Failed to prepare SQL statement"]);
    }
} else {
    $sql = "SELECT * FROM certificates";
    $result = $conn->query($sql);
    $certificates = [];
    if ($result) {
        while ($row = $result->fetch_assoc()) {
            $certificates[] = $row;
        }
    }
    echo json_encode($certificates);
}

$conn->close();
?>
