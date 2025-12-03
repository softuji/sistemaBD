// config.js - SEM JWT, sem Mock
const CONFIG = {
    APP: {
        NAME: 'Academia Fit',
        VERSION: '1.0.0'
    },
    API: {
        BASE_URL: 'http://localhost:8080',
        TIMEOUT: 10000
    },
    ENDPOINTS: {
        // AUTENTICAÇÃO SIMPLES (query params)
        AUTH: {
            LOGIN: '/usuario/login', // POST com body
            REGISTER: '/usuario/cadastro' // POST com body
        },
        
        // USUÁRIOS (sem auth necessária)
        USUARIO: {
            BUSCAR_POR_EMAIL: '/usuario', // GET ?email=
            LISTAR_TODOS: '/usuario/todos',
            ATUALIZAR: '/usuario/{id}', // PUT
            DELETAR: '/usuario' // DELETE ?email=
        },
        
        // PLANOS (públicos)
        PLANOS: {
            LISTAR: '/usuario/planos',
            BUSCAR_POR_TIPO: '/usuario/planos/{tipo}',
            CRIAR_BASICO: '/usuario/planos/basico',
            CRIAR_PREMIUM: '/usuario/planos/premium',
            CRIAR_TOP: '/usuario/planos/top'
        },
        
        // TREINOS (alguns podem precisar de ID de instrutor)
        TREINOS: {
            BUSCAR_TREINO_USUARIO: '/usuario/{usuarioId}/treinos',
            CRIAR_TREINO: '/usuario/{usuarioId}/treinos', // POST ?instrutorId=&nome=&objetivo=
            LISTAR_ATIVOS: '/usuario/treinos/ativos',
            ADICIONAR_EXERCICIO: '/usuario/{usuarioId}/treinos/exercicios' // POST com body JSON
        },
        
        // AVALIAÇÕES
        AVALIACOES: {
            BUSCAR_HISTORICO: '/usuario/{usuarioId}/avaliacoes',
            ADICIONAR_AVALIACAO: '/usuario/{usuarioId}/avaliacoes' // POST ?instrutorId=&peso=&altura=&observacoes=
        },
        
        // PROGRESSO
        PROGRESSO: {
            REGISTRAR: '/usuario/{usuarioId}/progresso',
            BUSCAR_GRAFICOS: '/usuario/{usuarioId}/graficos'
        },
        
        // EXERCÍCIOS (públicos)
        EXERCICIOS: '/exercicio/todos'
    },
    
    STORAGE: {
        USER_KEY: 'academia_user', // Apenas salva o objeto usuário completo
        USER_ID_KEY: 'academia_user_id'
        // SEM TOKEN_KEY porque não tem JWT
    },
    
    // Usuários de teste
    TEST_USERS: [
        { email: 'sofianemotto@email.com', senha: '467913', nome: 'Sofia Nemotto', tipo: 'ALUNO' },
        { email: 'jandersonmesquita@email.com', senha: '362514', nome: 'Janderson Mesquita', tipo: 'INSTRUTOR' },
        { email: 'carolvaz@email.com', senha: '147852', nome: 'Carol Vaz', tipo: 'ALUNO' }
    ]
};

// Função para obter headers básicos (sem autenticação JWT)
CONFIG.getHeaders = function() {
    return {
        'Content-Type': 'application/json'
        // SEM Authorization header
    };
};

// Função para construir URLs (identifica se usa query params ou body)
CONFIG.buildUrl = function(endpoint, params = {}) {
    let url = endpoint;
    const queryParams = new URLSearchParams();
    
    // Endpoints que usam query parameters (não body)
    const endpointsComQueryParams = [
        '/usuario?',           // GET /usuario?email=
        '/usuario/{id}/avaliacoes?',  // POST com query params
        '/usuario/{id}/treinos?'      // POST com query params (não /exercicios)
    ];
    
    const usaQueryParams = endpointsComQueryParams.some(ep => endpoint.includes(ep));
    
    // Substitui parâmetros no path
    for (const [key, value] of Object.entries(params)) {
        if (url.includes(`{${key}}`)) {
            url = url.replace(`{${key}}`, encodeURIComponent(value));
        } else if (usaQueryParams && value !== undefined && value !== null) {
            queryParams.append(key, value);
        }
    }
    
    // Adiciona query string se necessário
    const queryString = queryParams.toString();
    if (queryString && usaQueryParams) {
        url += `?${queryString}`;
    }
    
    return this.API.BASE_URL + url;
};

// Funções de gerenciamento de usuário
CONFIG.getCurrentUser = function() {
    const userStr = localStorage.getItem(this.STORAGE.USER_KEY);
    if (!userStr) return null;
    
    try {
        return JSON.parse(userStr);
    } catch (error) {
        console.error('Erro ao parsear usuário:', error);
        return null;
    }
};

CONFIG.saveUserData = function(userData) {
    if (userData) {
        localStorage.setItem(this.STORAGE.USER_KEY, JSON.stringify(userData));
        if (userData.id) {
            localStorage.setItem(this.STORAGE.USER_ID_KEY, userData.id);
        }
    }
};

CONFIG.clearUserData = function() {
    localStorage.removeItem(this.STORAGE.USER_KEY);
    localStorage.removeItem(this.STORAGE.USER_ID_KEY);
};

window.CONFIG = CONFIG;