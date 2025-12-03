// apiService.js - COM CORREÇÃO PARA @RequestBody NO LOGIN
class ApiService {
    constructor() {
        this.baseUrl = CONFIG.API.BASE_URL;
    }

    // ========== MÉTODOS DE AUTENTICAÇÃO SIMPLES ==========
    
    /**
     * Login COM @RequestBody (JSON no body)
     * ATUALIZADO: Agora usa body JSON em vez de query params
     * Exemplo: POST /usuario/login com body: {"email": "...", "senha": "..."}
     */
    async login(email, senha) {
        try {
            // Monta URL SEM query params
            const url = `${this.baseUrl}${CONFIG.ENDPOINTS.AUTH.LOGIN}`;
            
            // Dados para enviar no body (JSON)
            const bodyData = {
                email: email,
                senha: senha
            };
            
            console.log('🔐 Fazendo login:', url);
            console.log('📦 Body enviado:', bodyData);
            
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(bodyData)
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('❌ Erro no login:', response.status, errorText);
                throw new Error(`Login falhou (${response.status}): ${errorText || 'Credenciais inválidas'}`);
            }

            const usuario = await response.json();
            console.log('✅ Login bem-sucedido:', usuario);
            
            // Salva apenas os dados do usuário (SEM TOKEN JWT)
            CONFIG.saveUserData(usuario);
            
            return usuario;
            
        } catch (error) {
            console.error('💥 Erro no login:', error);
            throw error;
        }
    }

    /**
     * Cadastro de usuário (já usava body JSON)
     */
    async cadastrar(usuarioData) {
        try {
            const url = `${this.baseUrl}${CONFIG.ENDPOINTS.AUTH.REGISTER}`;
            
            console.log('📝 Cadastrando usuário:', url);
            console.log('📦 Body enviado:', usuarioData);
            
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(usuarioData)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Cadastro falhou: ${response.status} - ${errorText}`);
            }

            const usuario = await response.json();
            CONFIG.saveUserData(usuario);
            
            return usuario;
            
        } catch (error) {
            console.error('Erro no cadastro:', error);
            throw error;
        }
    }

    /**
     * Logout simples
     */
    logout() {
        CONFIG.clearUserData();
        return { success: true, message: 'Logout realizado' };
    }

    // ========== USUÁRIOS ==========
    
    async buscarUsuarioPorEmail(email) {
        try {
            // Este endpoint usa query parameter ?email=
            const url = `${this.baseUrl}${CONFIG.ENDPOINTS.USUARIO.BUSCAR_POR_EMAIL}?email=${encodeURIComponent(email)}`;
            
            console.log('🔍 Buscando usuário por email:', url);
            
            const response = await fetch(url, {
                method: 'GET',
                headers: CONFIG.getHeaders()
            });

            if (!response.ok) {
                throw new Error(`Erro ao buscar usuário: ${response.status}`);
            }

            return await response.json();
            
        } catch (error) {
            console.error('Erro ao buscar usuário:', error);
            throw error;
        }
    }

    async listarTodosUsuarios() {
        try {
            const url = `${this.baseUrl}${CONFIG.ENDPOINTS.USUARIO.LISTAR_TODOS}`;
            
            const response = await fetch(url, {
                method: 'GET',
                headers: CONFIG.getHeaders()
            });

            if (!response.ok) {
                throw new Error(`Erro ao listar usuários: ${response.status}`);
            }

            return await response.json();
            
        } catch (error) {
            console.error('Erro ao listar usuários:', error);
            throw error;
        }
    }

    // ========== PLANOS (PÚBLICOS) ==========
    
    async buscarPlanos() {
        try {
            const url = `${this.baseUrl}${CONFIG.ENDPOINTS.PLANOS.LISTAR}`;
            
            const response = await fetch(url, {
                method: 'GET',
                headers: CONFIG.getHeaders()
            });

            if (!response.ok) {
                throw new Error(`Erro ao buscar planos: ${response.status}`);
            }

            return await response.json();
            
        } catch (error) {
            console.error('Erro ao buscar planos:', error);
            throw error;
        }
    }

    async buscarPlanoPorTipo(tipo) {
        try {
            const url = `${this.baseUrl}${CONFIG.ENDPOINTS.PLANOS.BUSCAR_POR_TIPO.replace('{tipo}', tipo)}`;
            
            const response = await fetch(url, {
                method: 'GET',
                headers: CONFIG.getHeaders()
            });

            if (!response.ok) {
                throw new Error(`Erro ao buscar plano: ${response.status}`);
            }

            return await response.json();
            
        } catch (error) {
            console.error('Erro ao buscar plano:', error);
            throw error;
        }
    }

    // ========== TREINOS ==========
    
    async buscarTreinoUsuario(usuarioId) {
        try {
            const url = `${this.baseUrl}${CONFIG.ENDPOINTS.TREINOS.BUSCAR_TREINO_USUARIO.replace('{usuarioId}', usuarioId)}`;
            
            const response = await fetch(url, {
                method: 'GET',
                headers: CONFIG.getHeaders()
            });

            if (!response.ok) {
                throw new Error(`Erro ao buscar treino: ${response.status}`);
            }

            return await response.json();
            
        } catch (error) {
            console.error('Erro ao buscar treino:', error);
            throw error;
        }
    }

    async criarTreino(usuarioId, instrutorId, nome, objetivo) {
        try {
            // Este endpoint usa query parameters
            const url = `${this.baseUrl}${CONFIG.ENDPOINTS.TREINOS.CRIAR_TREINO.replace('{usuarioId}', usuarioId)}?instrutorId=${instrutorId}&nome=${encodeURIComponent(nome)}&objetivo=${encodeURIComponent(objetivo)}`;
            
            console.log('🏋️ Criando treino:', url);
            
            const response = await fetch(url, {
                method: 'POST',
                headers: CONFIG.getHeaders()
            });

            if (!response.ok) {
                throw new Error(`Erro ao criar treino: ${response.status}`);
            }

            return { success: true };
            
        } catch (error) {
            console.error('Erro ao criar treino:', error);
            throw error;
        }
    }

    // ========== AVALIAÇÕES ==========
    
    async adicionarAvaliacao(usuarioId, instrutorId, peso, altura, observacoes = '') {
        try {
            // Este endpoint usa query parameters
            let url = `${this.baseUrl}${CONFIG.ENDPOINTS.AVALIACOES.ADICIONAR_AVALIACAO.replace('{usuarioId}', usuarioId)}?instrutorId=${instrutorId}&peso=${peso}&altura=${altura}`;
            
            if (observacoes && observacoes.trim() !== '') {
                url += `&observacoes=${encodeURIComponent(observacoes)}`;
            }
            
            console.log('📊 Adicionando avaliação:', url);
            
            const response = await fetch(url, {
                method: 'POST',
                headers: CONFIG.getHeaders()
            });

            if (!response.ok) {
                throw new Error(`Erro ao adicionar avaliação: ${response.status}`);
            }

            return { success: true };
            
        } catch (error) {
            console.error('Erro ao adicionar avaliação:', error);
            throw error;
        }
    }

    async buscarAvaliacoesUsuario(usuarioId) {
        try {
            const url = `${this.baseUrl}${CONFIG.ENDPOINTS.AVALIACOES.BUSCAR_HISTORICO.replace('{usuarioId}', usuarioId)}`;
            
            const response = await fetch(url, {
                method: 'GET',
                headers: CONFIG.getHeaders()
            });

            if (!response.ok) {
                throw new Error(`Erro ao buscar avaliações: ${response.status}`);
            }

            return await response.json();
            
        } catch (error) {
            console.error('Erro ao buscar avaliações:', error);
            throw error;
        }
    }

    // ========== EXERCÍCIOS (PÚBLICOS) ==========
    
    async buscarExercicios() {
        try {
            const url = `${this.baseUrl}${CONFIG.ENDPOINTS.EXERCICIOS}`;
            
            const response = await fetch(url, {
                method: 'GET',
                headers: CONFIG.getHeaders()
            });

            if (!response.ok) {
                throw new Error(`Erro ao buscar exercícios: ${response.status}`);
            }

            return await response.json();
            
        } catch (error) {
            console.error('Erro ao buscar exercícios:', error);
            throw error;
        }
    }

    // ========== PROGRESSO E GRÁFICOS ==========
    
    async registrarProgresso(usuarioId, progressoData) {
        try {
            const url = `${this.baseUrl}${CONFIG.ENDPOINTS.PROGRESSO.REGISTRAR.replace('{usuarioId}', usuarioId)}`;
            
            console.log('📈 Registrando progresso:', url);
            console.log('📦 Body enviado:', progressoData);
            
            const response = await fetch(url, {
                method: 'POST',
                headers: CONFIG.getHeaders(),
                body: JSON.stringify(progressoData)
            });

            if (!response.ok) {
                throw new Error(`Erro ao registrar progresso: ${response.status}`);
            }

            return { success: true };
            
        } catch (error) {
            console.error('Erro ao registrar progresso:', error);
            throw error;
        }
    }

    async buscarDadosGraficos(usuarioId) {
        try {
            const url = `${this.baseUrl}${CONFIG.ENDPOINTS.PROGRESSO.BUSCAR_GRAFICOS.replace('{usuarioId}', usuarioId)}`;
            
            const response = await fetch(url, {
                method: 'GET',
                headers: CONFIG.getHeaders()
            });

            if (!response.ok) {
                throw new Error(`Erro ao buscar dados gráficos: ${response.status}`);
            }

            return await response.json();
            
        } catch (error) {
            console.error('Erro ao buscar dados gráficos:', error);
            throw error;
        }
    }

    // ========== MÉTODOS AUXILIARES ==========
    
    /**
     * Verifica se o usuário está autenticado
     */
    isAuthenticated() {
        return !!CONFIG.getCurrentUser();
    }

    /**
     * Obtém o usuário atual
     */
    getCurrentUser() {
        return CONFIG.getCurrentUser();
    }

    /**
     * Obtém o ID do usuário atual
     */
    getCurrentUserId() {
        const user = this.getCurrentUser();
        return user ? user.id : null;
    }

    /**
     * Verifica se o usuário atual é instrutor
     */
    isInstructor() {
        const user = this.getCurrentUser();
        return user ? user.tipo === 'INSTRUTOR' : false;
    }

    /**
     * Verifica se o usuário atual é admin
     */
    isAdmin() {
        const user = this.getCurrentUser();
        return user ? user.tipo === 'ADMIN' : false;
    }
    
    isAluno() {
        const user = this.getCurrentUser();
        return user ? user.tipo === 'ALUNO' : false;
    }

    /**
     * Gera comando curl para testar login no terminal
     */
    gerarCurlLogin(email, senha) {
        return `curl -X POST "${this.baseUrl}${CONFIG.ENDPOINTS.AUTH.LOGIN}" \\
  -H "Content-Type: application/json" \\
  -d '{"email": "${email}", "senha": "${senha}"}'`;
    }

    /**
     * Gera comando para Postman
     */
    gerarPostmanLogin(email, senha) {
        return {
            method: 'POST',
            url: `${this.baseUrl}${CONFIG.ENDPOINTS.AUTH.LOGIN}`,
            headers: {
                'Content-Type': 'application/json'
            },
            body: {
                mode: 'raw',
                raw: JSON.stringify({ email, senha }, null, 2)
            }
        };
    }

    /**
     * Método de teste rápido
     */
    async testarConexao() {
        try {
            console.log('🔗 Testando conexão com backend...');
            const planos = await this.buscarPlanos();
            console.log('✅ Conexão OK. Planos encontrados:', planos.length);
            return planos;
        } catch (error) {
            console.error('❌ Erro na conexão:', error.message);
            throw error;
        }
    }
}

window.ApiService = ApiService;