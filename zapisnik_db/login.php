<?php
header('Content-Type: application/json');

// Načítajte pripojovací URL z JAWSDB_URL (pre Heroku addon JAWSDB)
$dbUrl = getenv('JAWSDB_URL');
if (!$dbUrl) {
    die(json_encode(['success' => false, 'message' => 'JAWSDB environment variable not set.']));
}

// Rozparsujte URL na jednotlivé časti
$dbparts = parse_url($dbUrl);
$servername = $dbparts['host'];
$username = $dbparts['user'];
$password = $dbparts['pass'];
$dbname = ltrim($dbparts['path'], '/');

// Vytvorte pripojenie
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die(json_encode(['success' => false, 'message' => "Connection failed: " . $conn->connect_error]));
}

// Spracovanie POST požiadavky pre prihlásenie
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $usernameOrEmail = trim($_POST['usernameOrEmail']);
    $passwordInput = $_POST['password'];

    // Pripravte dotaz pre vyhľadanie používateľa podľa username alebo emailu
    $stmt = $conn->prepare("SELECT id, username, email, password FROM users WHERE username = ? OR email = ?");
    if (!$stmt) {
        echo json_encode(['success' => false, 'message' => 'Database error.']);
        exit;
    }
    $stmt->bind_param("ss", $usernameOrEmail, $usernameOrEmail);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        $user = $result->fetch_assoc();
        // Overenie hesla
        if (password_verify($passwordInput, $user['password'])) {
            unset($user['password']); // Odstrániť citlivé údaje
            echo json_encode(['success' => true, 'message' => 'Login successful', 'user' => $user]);
        } else {
            echo json_encode(['success' => false, 'message' => 'Invalid password']);
        }
    } else {
        echo json_encode(['success' => false, 'message' => 'User not found']);
    }
    $stmt->close();
    $conn->close();
} else {
    echo json_encode(['success' => false, 'message' => 'Invalid request']);
}
?>
