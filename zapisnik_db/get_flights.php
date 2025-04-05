<?php
header("Content-Type: application/json");

// Načítajte pripojovacie údaje z environmentálnej premennej JAWSDB_URL (Heroku) alebo použite lokálne nastavenia
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

// Vytvorte pripojenie k databáze
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["error" => "Connection failed: " . $conn->connect_error]);
    exit();
}

// Voliteľný filter podľa user_id (ak je zadaný)
$userIdFilter = "";
if (isset($_GET['user_id']) && !empty($_GET['user_id'])) {
    $userId = intval($_GET['user_id']);
    $userIdFilter = " WHERE user_id = $userId";
}

// SQL dotaz pre načítanie dát z tabuľky flights
$sql = "SELECT id, date, departure_place, departure_time, arrival_place, arrival_time, aircraft_model, registration, 
        single_pilot_time, multi_pilot_time, total_flight_time, pilot_name, single_pilot, landings_day, landings_night, 
        night_time, ifr_time, pic_time, copilot_time, dual_time, instructor_time, fstd_date, fstd_type, fstd_total_time, remarks, user_id 
        FROM flights" . $userIdFilter;

$result = $conn->query($sql);

// Inicializujte pole pre lety
$flights = array();
if ($result && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $flights[] = $row;
    }
} else {
    // Ak výsledok nie je platný alebo nie sú záznamy, vráťte prázdne pole
    $flights = [];
}

// Vrátenie dát vo formáte JSON
echo json_encode($flights);
$conn->close();
?>
