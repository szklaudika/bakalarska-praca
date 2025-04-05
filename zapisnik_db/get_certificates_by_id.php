<?php
header("Content-Type: application/json");

// Načítajte pripojovacie údaje z JAWSDB_URL, ak je nastavená, inak použite lokálne nastavenia.
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

// Vytvorte pripojenie
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die(json_encode(["error" => "Database connection failed: " . $conn->connect_error]));
}

// Skontrolujte, či je parameter 'id' poskytnutý v URL
if (isset($_GET['id'])) {
    $id = intval($_GET['id']); // Zabezpečíme, že ID je celé číslo

    // Logovanie pre debugging (záznam do logov na Heroku)
    error_log("Received request to fetch certificate with ID: $id");

    // Pripravte SQL dotaz na získanie certifikátu podľa ID
    $sql = "SELECT * FROM certificates WHERE id = ?";
    $stmt = $conn->prepare($sql);

    if ($stmt) {
        $stmt->bind_param("i", $id);  // Prepojenie parametra typu integer
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result && $result->num_rows > 0) {
            // Načítame údaje certifikátu
            $certificate = $result->fetch_assoc();
            echo json_encode($certificate);
        } else {
            echo json_encode(["error" => "Certificate not found"]);
        }

        $stmt->close();
    } else {
        echo json_encode(["error" => "Failed to prepare SQL statement"]);
    }
} else {
    echo json_encode(["error" => "ID parameter is missing"]);
}

$conn->close();
?>
