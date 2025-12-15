const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
require('dotenv').config();

const db = require('./db');

const app = express();
const PORT = process.env.PORT || 3000;

// Middlewares
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// ==================== ROTAS DA API ====================

// 1. CRIAR NOTA
app.post('/api/notas', async (req, res) => {
    try {
        const { titulo, conteudo, latitude, longitude, endereco } = req.body;

        // Validação básica
        if (!titulo || !conteudo) {
            return res.status(400).json({ 
                error: 'Título e conteúdo são obrigatórios' 
            });
        }

        const query = `
            INSERT INTO notas (titulo, conteudo, latitude, longitude, endereco, data_criacao)
            VALUES (?, ?, ?, ?, ?, NOW())
        `;

        const [result] = await db.execute(query, [
            titulo,
            conteudo,
            latitude || null,
            longitude || null,
            endereco || null
        ]);

        res.status(201).json({
            success: true,
            message: 'Nota criada com sucesso!',
            id: result.insertId
        });

    } catch (error) {
        console.error('Erro ao criar nota:', error);
        res.status(500).json({ 
            error: 'Erro ao criar nota',
            details: error.message 
        });
    }
});

// 2. LISTAR TODAS AS NOTAS
app.get('/api/notas', async (req, res) => {
    try {
        const query = `
            SELECT id, titulo, conteudo, latitude, longitude, endereco, data_criacao
            FROM notas
            ORDER BY data_criacao DESC
        `;

        const [rows] = await db.execute(query);

        res.json({
            success: true,
            total: rows.length,
            notas: rows
        });

    } catch (error) {
        console.error('Erro ao listar notas:', error);
        res.status(500).json({ 
            error: 'Erro ao listar notas',
            details: error.message 
        });
    }
});

// 3. OBTER NOTA POR ID
app.get('/api/notas/:id', async (req, res) => {
    try {
        const { id } = req.params;

        const query = `
            SELECT id, titulo, conteudo, latitude, longitude, endereco, data_criacao
            FROM notas
            WHERE id = ?
        `;

        const [rows] = await db.execute(query, [id]);

        if (rows.length === 0) {
            return res.status(404).json({ 
                error: 'Nota não encontrada' 
            });
        }

        res.json({
            success: true,
            nota: rows[0]
        });

    } catch (error) {
        console.error('Erro ao obter nota:', error);
        res.status(500).json({ 
            error: 'Erro ao obter nota',
            details: error.message 
        });
    }
});

// 4. ATUALIZAR NOTA
app.put('/api/notas/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const { titulo, conteudo, latitude, longitude, endereco } = req.body;

        const query = `
            UPDATE notas
            SET titulo = ?, conteudo = ?, latitude = ?, longitude = ?, endereco = ?
            WHERE id = ?
        `;

        const [result] = await db.execute(query, [
            titulo,
            conteudo,
            latitude || null,
            longitude || null,
            endereco || null,
            id
        ]);

        if (result.affectedRows === 0) {
            return res.status(404).json({ 
                error: 'Nota não encontrada' 
            });
        }

        res.json({
            success: true,
            message: 'Nota atualizada com sucesso!'
        });

    } catch (error) {
        console.error('Erro ao atualizar nota:', error);
        res.status(500).json({ 
            error: 'Erro ao atualizar nota',
            details: error.message 
        });
    }
});

// 5. ELIMINAR NOTA
app.delete('/api/notas/:id', async (req, res) => {
    try {
        const { id } = req.params;

        const query = 'DELETE FROM notas WHERE id = ?';
        const [result] = await db.execute(query, [id]);

        if (result.affectedRows === 0) {
            return res.status(404).json({ 
                error: 'Nota não encontrada' 
            });
        }

        res.json({
            success: true,
            message: 'Nota eliminada com sucesso!'
        });

    } catch (error) {
        console.error('Erro ao eliminar nota:', error);
        res.status(500).json({ 
            error: 'Erro ao eliminar nota',
            details: error.message 
        });
    }
});

// 6. BUSCAR NOTAS POR LOCALIZAÇÃO (OPCIONAL)
app.get('/api/notas/localizacao/:endereco', async (req, res) => {
    try {
        const { endereco } = req.params;

        const query = `
            SELECT id, titulo, conteudo, latitude, longitude, endereco, data_criacao
            FROM notas
            WHERE endereco LIKE ?
            ORDER BY data_criacao DESC
        `;

        const [rows] = await db.execute(query, [`%${endereco}%`]);

        res.json({
            success: true,
            total: rows.length,
            notas: rows
        });

    } catch (error) {
        console.error('Erro ao buscar notas:', error);
        res.status(500).json({ 
            error: 'Erro ao buscar notas',
            details: error.message 
        });
    }
});

// Rota de teste
app.get('/', (req, res) => {
    res.json({ 
        message: 'API de Notas está funcionando!',
        endpoints: {
            'POST /api/notas': 'Criar nota',
            'GET /api/notas': 'Listar todas as notas',
            'GET /api/notas/:id': 'Obter nota por ID',
            'PUT /api/notas/:id': 'Atualizar nota',
            'DELETE /api/notas/:id': 'Eliminar nota',
            'GET /api/notas/localizacao/:endereco': 'Buscar por localização'
        }
    });
});

// Iniciar servidor
app.listen(PORT, () => {
    console.log(`Servidor rodando na porta ${PORT}`);
    console.log(`Acesse: http://localhost:${PORT}`);
});