<?php
header("Content-Type: application/json");

// Použite pripojovacie údaje z JAWSDB_URL, ak je nastavená (Heroku), inak lokálne
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

// Vytvorenie pripojenia
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Connection failed: " . $conn->connect_error]);
    exit();
}

// Načítanie údajov zo vstupu (skúsi JSON, ak prázdne, použije $_POST)
$data = json_decode(file_get_contents("php://input"), true);
if (empty($data)) {
    $data = $_POST;
}

if (!empty($data)) {
    // Predpokladáme, že údaje obsahujú 'email', 'username', 'password' a ďalšie polia.
    $email = isset($data['email']) ? $data['email'] : '';
    $usernameInput = isset($data['username']) ? $data['username'] : '';
    $passwordInput = isset($data['password']) ? $data['password'] : '';

    // Kontrola povinných polí
    if(empty($email) || empty($usernameInput) || empty($passwordInput)){
        http_response_code(400);
        echo json_encode(["status" => "error", "message" => "Required fields missing"]);
        exit();
    }
    
    // Zahashovanie hesla
    $hashedPassword = password_hash($passwordInput, PASSWORD_DEFAULT);

    // Overenie, či používateľ s týmto e-mailom už existuje
    $checkStmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
    if($checkStmt){
        $checkStmt->bind_param("s", $email);
        $checkStmt->execute();
        $checkResult = $checkStmt->get_result();
        if ($checkResult->num_rows > 0) {
            echo json_encode(["status" => "error", "message" => "User with this email already exists"]);
            $checkStmt->close();
            $conn->close();
            exit();
        }
        $checkStmt->close();
    }
    
    // Príprava SQL dotazu na vloženie nového používateľa
    $stmt = $conn->prepare("INSERT INTO users (username, email, password) VALUES (?, ?, ?)");
    if (!$stmt) {
        http_response_code(500);
        echo json_encode(["status" => "error", "message" => "Database error: " . $conn->error]);
        exit();
    }
    $stmt->bind_param("sss", $usernameInput, $email, $hashedPassword);
    
    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "User registered successfully"]);
    } else {
        http_response_code(500);
        echo json_encode(["status" => "error", "message" => "Registration failed: " . $stmt->error]);
    }
    
    $stmt->close();
} else {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "No data received"]);
}

$conn->close();
?>
