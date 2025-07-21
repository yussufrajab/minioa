package com.zanzibar.csms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:9002", "http://localhost:3000"})
public class SimpleUserController {
    
    @GetMapping("/hello")
    public ResponseEntity<Map<String, Object>> hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from SimpleUserController!");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        try {
            String sql = """
                SELECT u.id, u.name, u.username, u.role, u.active, u."institutionId",
                       i.name as institution_name
                FROM "User" u 
                LEFT JOIN "Institution" i ON u."institutionId" = i.id
                ORDER BY u.name
                """;
            
            List<Map<String, Object>> users = jdbcTemplate.query(sql, new RowMapper<Map<String, Object>>() {
                @Override
                public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Map<String, Object> user = new HashMap<>();
                    user.put("id", rs.getString("id"));
                    user.put("name", rs.getString("name"));
                    user.put("username", rs.getString("username"));
                    user.put("role", rs.getString("role"));
                    user.put("active", rs.getBoolean("active"));
                    user.put("institutionId", rs.getString("institutionId"));
                    user.put("institution", rs.getString("institution_name"));
                    return user;
                }
            });
            
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/institutions")
    public ResponseEntity<List<Map<String, Object>>> getAllInstitutions() {
        try {
            String sql = "SELECT id, name FROM \"Institution\" ORDER BY name";
            
            List<Map<String, Object>> institutions = jdbcTemplate.query(sql, new RowMapper<Map<String, Object>>() {
                @Override
                public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Map<String, Object> institution = new HashMap<>();
                    institution.put("id", rs.getString("id"));
                    institution.put("name", rs.getString("name"));
                    return institution;
                }
            });
            
            return ResponseEntity.ok(institutions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            if (username == null || password == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Username and password are required");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Simple authentication - check if user exists and is active
            String sql = """
                SELECT u.id, u.name, u.username, u.role, u.active, u."institutionId",
                       u."createdAt", u."updatedAt", i.name as institution_name
                FROM "User" u 
                LEFT JOIN "Institution" i ON u."institutionId" = i.id
                WHERE u.username = ? AND u.active = true
                """;
            
            List<Map<String, Object>> users = jdbcTemplate.query(sql, new RowMapper<Map<String, Object>>() {
                @Override
                public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Map<String, Object> user = new HashMap<>();
                    user.put("id", rs.getString("id"));
                    user.put("name", rs.getString("name"));
                    user.put("username", rs.getString("username"));
                    user.put("role", rs.getString("role"));
                    user.put("active", rs.getBoolean("active"));
                    user.put("institutionId", rs.getString("institutionId"));
                    user.put("createdAt", rs.getTimestamp("createdAt"));
                    user.put("updatedAt", rs.getTimestamp("updatedAt"));
                    user.put("institution", rs.getString("institution_name"));
                    return user;
                }
            }, username);
            
            if (users.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Invalid username or password");
                return ResponseEntity.status(401).body(error);
            }
            
            Map<String, Object> user = users.get(0);
            
            // Create response with user data
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", user);
            response.put("message", "Login successful");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/institutions")
    public ResponseEntity<Map<String, Object>> createInstitution(@RequestBody Map<String, String> institutionRequest) {
        try {
            String name = institutionRequest.get("name");
            
            if (name == null || name.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Institution name is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Generate a simple ID for the institution
            String id = "inst_" + System.currentTimeMillis();
            
            String sql = "INSERT INTO \"Institution\" (id, name) VALUES (?, ?)";
            
            int result = jdbcTemplate.update(sql, id, name.trim());
            
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                Map<String, Object> institution = new HashMap<>();
                institution.put("id", id);
                institution.put("name", name.trim());
                
                response.put("success", true);
                response.put("data", institution);
                response.put("message", "Institution created successfully");
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Failed to create institution");
                return ResponseEntity.internalServerError().body(error);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PutMapping("/institutions/{id}")
    public ResponseEntity<Map<String, Object>> updateInstitution(@PathVariable String id, @RequestBody Map<String, String> institutionRequest) {
        try {
            String name = institutionRequest.get("name");
            
            if (name == null || name.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Institution name is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            String sql = "UPDATE \"Institution\" SET name = ? WHERE id = ?";
            
            int result = jdbcTemplate.update(sql, name.trim(), id);
            
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                Map<String, Object> institution = new HashMap<>();
                institution.put("id", id);
                institution.put("name", name.trim());
                
                response.put("success", true);
                response.put("data", institution);
                response.put("message", "Institution updated successfully");
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Institution not found");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @DeleteMapping("/institutions/{id}")
    public ResponseEntity<Map<String, Object>> deleteInstitution(@PathVariable String id) {
        try {
            String sql = "DELETE FROM \"Institution\" WHERE id = ?";
            
            int result = jdbcTemplate.update(sql, id);
            
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Institution deleted successfully");
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Institution not found");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> userRequest) {
        try {
            String name = (String) userRequest.get("name");
            String username = (String) userRequest.get("username");
            String role = (String) userRequest.get("role");
            String institutionId = (String) userRequest.get("institutionId");
            Boolean active = (Boolean) userRequest.get("active");
            
            if (name == null || name.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Name is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (username == null || username.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Username is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (role == null || role.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Role is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (institutionId == null || institutionId.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Institution is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Check if username already exists
            String checkSql = "SELECT COUNT(*) FROM \"User\" WHERE username = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, username);
            
            if (count != null && count > 0) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Username already exists");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Generate a simple ID for the user
            String id = "user_" + System.currentTimeMillis();
            
            String sql = """
                INSERT INTO "User" (id, name, username, password, role, active, "institutionId", "createdAt", "updatedAt") 
                VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
                """;
            
            // Use a default password for now - in production this should be properly hashed
            String defaultPassword = "defaultpassword";
            
            int result = jdbcTemplate.update(sql, id, name.trim(), username.trim(), defaultPassword, role.trim(), 
                                           active != null ? active : true, institutionId.trim());
            
            if (result > 0) {
                // Fetch the created user with institution info
                String selectSql = """
                    SELECT u.id, u.name, u.username, u.role, u.active, u."institutionId",
                           u."createdAt", u."updatedAt", i.name as institution_name
                    FROM "User" u 
                    LEFT JOIN "Institution" i ON u."institutionId" = i.id
                    WHERE u.id = ?
                    """;
                
                List<Map<String, Object>> users = jdbcTemplate.query(selectSql, new RowMapper<Map<String, Object>>() {
                    @Override
                    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Map<String, Object> user = new HashMap<>();
                        user.put("id", rs.getString("id"));
                        user.put("name", rs.getString("name"));
                        user.put("username", rs.getString("username"));
                        user.put("role", rs.getString("role"));
                        user.put("active", rs.getBoolean("active"));
                        user.put("institutionId", rs.getString("institutionId"));
                        user.put("createdAt", rs.getTimestamp("createdAt"));
                        user.put("updatedAt", rs.getTimestamp("updatedAt"));
                        user.put("institution", rs.getString("institution_name"));
                        return user;
                    }
                }, id);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", users.get(0));
                response.put("message", "User created successfully");
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Failed to create user");
                return ResponseEntity.internalServerError().body(error);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable String id, @RequestBody Map<String, Object> userRequest) {
        try {
            // First check if user exists
            String checkUserSql = "SELECT COUNT(*) FROM \"User\" WHERE id = ?";
            Integer userExists = jdbcTemplate.queryForObject(checkUserSql, Integer.class, id);
            
            if (userExists == null || userExists == 0) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "User not found");
                return ResponseEntity.status(404).body(error);
            }
            
            // Build dynamic SQL for partial updates
            StringBuilder sqlBuilder = new StringBuilder("UPDATE \"User\" SET \"updatedAt\" = NOW()");
            List<Object> params = new ArrayList<>();
            
            // Handle each field if present in request
            if (userRequest.containsKey("name")) {
                String name = (String) userRequest.get("name");
                if (name == null || name.trim().isEmpty()) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Name cannot be empty");
                    return ResponseEntity.badRequest().body(error);
                }
                sqlBuilder.append(", name = ?");
                params.add(name.trim());
            }
            
            if (userRequest.containsKey("username")) {
                String username = (String) userRequest.get("username");
                if (username == null || username.trim().isEmpty()) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Username cannot be empty");
                    return ResponseEntity.badRequest().body(error);
                }
                
                // Check if username already exists (excluding current user)
                String checkSql = "SELECT COUNT(*) FROM \"User\" WHERE username = ? AND id != ?";
                Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, username.trim(), id);
                
                if (count != null && count > 0) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Username already exists");
                    return ResponseEntity.badRequest().body(error);
                }
                
                sqlBuilder.append(", username = ?");
                params.add(username.trim());
            }
            
            if (userRequest.containsKey("role")) {
                String role = (String) userRequest.get("role");
                if (role != null) {
                    sqlBuilder.append(", role = ?");
                    params.add(role.trim());
                }
            }
            
            if (userRequest.containsKey("active")) {
                Boolean active = (Boolean) userRequest.get("active");
                sqlBuilder.append(", active = ?");
                params.add(active != null ? active : true);
            }
            
            if (userRequest.containsKey("institutionId")) {
                String institutionId = (String) userRequest.get("institutionId");
                if (institutionId != null) {
                    sqlBuilder.append(", \"institutionId\" = ?");
                    params.add(institutionId.trim());
                }
            }
            
            sqlBuilder.append(" WHERE id = ?");
            params.add(id);
            
            int result = jdbcTemplate.update(sqlBuilder.toString(), params.toArray());
            
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "User updated successfully");
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "User not found");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String id) {
        try {
            String sql = "DELETE FROM \"User\" WHERE id = ?";
            
            int result = jdbcTemplate.update(sql, id);
            
            if (result > 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "User deleted successfully");
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "User not found");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> getNotifications(@RequestParam(required = false) String userId) {
        try {
            // For now, return empty notifications array
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", new ArrayList<>());
            response.put("message", "Notifications retrieved successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Internal server error");
            return ResponseEntity.internalServerError().body(error);
        }
    }
}