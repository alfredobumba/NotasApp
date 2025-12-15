-- Criar base de dados
CREATE DATABASE IF NOT EXISTS notes_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Usar a base de dados
USE notes_db;

-- Eliminar tabela se existir (apenas para desenvolvimento)
DROP TABLE IF EXISTS notas;

-- Criar tabela de notas
CREATE TABLE notas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    conteudo TEXT NOT NULL,
    latitude DOUBLE NULL,
    longitude DOUBLE NULL,
    endereco VARCHAR(500) NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_data_criacao (data_criacao),
    INDEX idx_endereco (endereco)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Inserir dados de exemplo (opcional)
INSERT INTO notas (titulo, conteudo, latitude, longitude, endereco) VALUES
('Primeira Nota', 'Esta é a minha primeira nota de teste!', 38.7223, -9.1393, 'Lisboa, Portugal'),
('Reunião Importante', 'Reunião com a equipa às 14h', 38.7436, -9.2302, 'Almada, Portugal'),
('Ideias para o Projeto', 'Implementar notificações push na próxima versão', 38.7071, -9.1355, 'Cacilhas, Almada');

-- Verificar dados inseridos
SELECT * FROM notas ORDER BY data_criacao DESC;