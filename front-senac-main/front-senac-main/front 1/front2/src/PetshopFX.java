import javafx.application.Application;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class PetshopFX extends Application {

    // Dados do sistema (mantidos em memória)
    private ObservableList<Cliente> clientes = FXCollections.observableArrayList();
    private ObservableList<Servico> servicos = FXCollections.observableArrayList();
    private ObservableList<Produto> racoes = FXCollections.observableArrayList();
    private ObservableList<Funcionario> funcionarios = FXCollections.observableArrayList();
    private ObservableList<Agendamento> agendamentos = FXCollections.observableArrayList();
    private ObservableList<String> recibos = FXCollections.observableArrayList();

    private Stage stage;
    private BorderPane mainRoot;
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final String[] HORARIOS_DISPONIVEIS = {"09:00", "10:00", "11:00", "14:00", "15:00", "16:00"};

    // tema: true = azul (padrão), false = laranja
    private boolean temaAzul = true;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        inicializarPadrao();
        mostrarLogin();
    }

    private void inicializarPadrao() {
        // Inicializar com os valores que você tinha no Main
        servicos.addAll(
                new Servico("Banho", 40.00),
                new Servico("Tosa", 60.00),
                new Servico("Tosa Higiênica", 35.00),
                new Servico("Hidratação", 50.00)
        );

        Produto p1 = new Produto("Ração Comum", "Ração padrão para cães adultos.", 20.00);
        Produto p2 = new Produto("Ração Premium", "Ração premium com mais nutrientes.", 35.00);
        Produto p3 = new Produto("Ração Super Premium", "Alto valor nutricional.", 55.00);
        Produto p4 = new Produto("Ração Filhote", "Especial para cães filhotes.", 25.00);
        Produto p5 = new Produto("Ração Sênior", "Ração para cães idosos.", 28.00);
        racoes.addAll(p1, p2, p3, p4, p5);

        funcionarios.addAll(
                new Funcionario("Ana Souza", "Atendente"),
                new Funcionario("Carlos Lima", "Atendente"),
                new Funcionario("Bruno Pereira", "Tosador"),
                new Funcionario("Mariana Alves", "Tosador"),
                new Funcionario("Rita Souza", "Banhista"),
                new Funcionario("Diego Rocha", "Banhista"),
                new Funcionario("João Ferreira", "Entregador"),
                new Funcionario("Paula Costa", "Entregador")
        );

        // exemplo de cliente + pet
        Cliente c = new Cliente("Pedro Silva", "pedro@email.com", "10-05-1990", "Rua A, 123");
        Pet pet = new Pet("Rex", "cachorro", 3);
        c.adicionarPet(pet);
        clientes.add(c);
    }

    /* ------------------ LOGIN ------------------ */
    private void mostrarLogin() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(30));
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("login-card");

        Label title = new Label("PetShop Premium");
        title.getStyleClass().add("login-title");

        TextField user = new TextField();
        user.setPromptText("Usuário");

        PasswordField pass = new PasswordField();
        pass.setPromptText("Senha");

        Button entrar = new Button("Entrar");
        entrar.getStyleClass().add("btn-primary");

        entrar.setOnAction(e -> {
            if ("admin".equals(user.getText()) && "1234".equals(pass.getText())) {
                mostrarDashboard();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Login inválido", "Usuário ou senha incorretos.");
            }
        });

        card.getChildren().addAll(title, user, pass, entrar);

        StackPane root = new StackPane(card);
        root.getStyleClass().add("login-bg");

        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(getClass().getResource("petshop.css").toExternalForm());

        // aplica tema atual à scene
        aplicarTema(scene);

        stage.setScene(scene);
        stage.setTitle("PetShop • Login");
        stage.show();
    }

    /* ------------------ DASHBOARD ------------------ */
    private void mostrarDashboard() {
        mainRoot = new BorderPane();

        VBox sidebar = new VBox(8);
        sidebar.setPadding(new Insets(16));
        sidebar.getStyleClass().add("sidebar");

        Button bClientes = criarBotaoSidebar("Clientes");
        Button bServicos = criarBotaoSidebar("Serviços");
        Button bRacoes = criarBotaoSidebar("Rações");
        Button bFuncionarios = criarBotaoSidebar("Funcionários");
        Button bAtendimento = criarBotaoSidebar("Iniciar Atendimento");
        Button bAgendamentos = criarBotaoSidebar("Agendamentos");
        Button bRecibos = criarBotaoSidebar("Recibos");
        Button bTema = criarBotaoSidebar("🎨 Trocar Tema");
        Button bSair = criarBotaoSidebar("Sair");

        bClientes.setOnAction(e -> mainRoot.setCenter(telaClientes()));
        bServicos.setOnAction(e -> mainRoot.setCenter(telaServicos()));
        bRacoes.setOnAction(e -> mainRoot.setCenter(telaRacoes()));
        bFuncionarios.setOnAction(e -> mainRoot.setCenter(telaFuncionarios()));
        bAtendimento.setOnAction(e -> mainRoot.setCenter(telaAtendimento()));
        bAgendamentos.setOnAction(e -> mainRoot.setCenter(telaAgendamentos()));
        bRecibos.setOnAction(e -> mainRoot.setCenter(telaRecibos()));

        bTema.setOnAction(e -> {
            alternarTema();
        });

        bSair.setOnAction(e -> {
            gerarRecibosAoSair(); // mostra no console e mantém recibos
            stage.close();
        });

        sidebar.getChildren().addAll(
                bClientes, bServicos, bRacoes, bFuncionarios,
                new Separator(), bAtendimento, bAgendamentos, bRecibos,
                new Separator(), bTema, bSair
        );

        mainRoot.setLeft(sidebar);
        mainRoot.setCenter(telaBemVindo());

        Scene scene = new Scene(mainRoot, 1200, 750);
        scene.getStylesheets().add(getClass().getResource("petshop.css").toExternalForm());

        // aplica tema atual à scene (dashboard)
        aplicarTema(scene);

        stage.setScene(scene);
        stage.setTitle("PetShop Premium");
        stage.show();
    }

    private Label criarTitulo(String texto) {
        Label l = new Label(texto);
        l.getStyleClass().add("title");
        return l;
    }

    private Button criarBotaoSidebar(String texto) {
        Button b = new Button(texto);
        b.getStyleClass().add("menu-btn");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    /* ------------------ TELAS (CRUD e funcionalidades) ------------------ */

    private Pane telaBemVindo() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        Label t1 = criarTitulo("Bem-vindo ao PetShop Premium");
        Label t2 = new Label("Use o menu à esquerda para gerenciar clientes, serviços, rações, funcionários e atendimentos.");
        box.getChildren().addAll(t1, t2);
        return box;
    }

    /* ---------- CLIENTES ---------- */
    private Pane telaClientes() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(16));

        TableView<Cliente> tabela = new TableView<>(clientes);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Cliente, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeCliente"));

        TableColumn<Cliente, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Cliente, String> colNascimento = new TableColumn<>("Nascimento");
        colNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));

        tabela.getColumns().addAll(colNome, colEmail, colNascimento);

        // Form de cadastro/edição
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        TextField tfNome = new TextField();
        TextField tfEmail = new TextField();
        TextField tfNascimento = new TextField();
        TextField tfEndereco = new TextField();

        Button btnCadastrar = new Button("Cadastrar");
        Button btnAtualizar = new Button("Atualizar Selecionado");
        Button btnRemover = new Button("Remover Selecionado");
        Button btnGerirPets = new Button("Gerir Pets");

        form.add(new Label("Nome:"), 0, 0);
        form.add(tfNome, 1, 0);
        form.add(new Label("Email:"), 0, 1);
        form.add(tfEmail, 1, 1);
        form.add(new Label("Nascimento (DD-MM-AAAA):"), 0, 2);
        form.add(tfNascimento, 1, 2);
        form.add(new Label("Endereço:"), 0, 3);
        form.add(tfEndereco, 1, 3);
        form.add(btnCadastrar, 1, 4);
        form.add(btnAtualizar, 1, 5);
        form.add(btnRemover, 1, 6);
        form.add(btnGerirPets, 1, 7);

        btnCadastrar.setOnAction(e -> {
            String nome = tfNome.getText().trim();
            if (nome.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Nome não pode ser vazio.");
                return;
            }
            Cliente c = new Cliente(nome, tfEmail.getText().trim(), tfNascimento.getText().trim(), tfEndereco.getText().trim());
            clientes.add(c);
            limparCampos(tfNome, tfEmail, tfNascimento, tfEndereco);
        });

        btnAtualizar.setOnAction(e -> {
            Cliente sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione um cliente para atualizar.");
                return;
            }
            sel.setNomeCliente(tfNome.getText().trim().isEmpty() ? sel.getNomeCliente() : tfNome.getText().trim());
            sel.setEmail(tfEmail.getText().trim().isEmpty() ? sel.getEmail() : tfEmail.getText().trim());
            sel.setDataNascimento(tfNascimento.getText().trim().isEmpty() ? sel.getDataNascimento() : tfNascimento.getText().trim());
            sel.setEndereco(tfEndereco.getText().trim().isEmpty() ? sel.getEndereco() : tfEndereco.getText().trim());
            tabela.refresh();
            limparCampos(tfNome, tfEmail, tfNascimento, tfEndereco);
        });

        btnRemover.setOnAction(e -> {
            Cliente sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione um cliente para remover.");
                return;
            }
            clientes.remove(sel);
        });

        btnGerirPets.setOnAction(e -> {
            Cliente sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione um cliente para gerir pets.");
                return;
            }
            Stage st = new Stage();
            st.setTitle("Pets de " + sel.getNomeCliente());
            st.setScene(new Scene(telaGerirPets(sel), 500, 400));
            aplicarTema(st.getScene()); // aplica tema à nova janela
            st.show();
        });

        // preencher campos ao selecionar
        tabela.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                tfNome.setText(newV.getNomeCliente());
                tfEmail.setText(newV.getEmail());
                tfNascimento.setText(newV.getDataNascimento());
                tfEndereco.setText(newV.getEndereco());
            }
        });

        pane.setCenter(tabela);
        pane.setRight(form);
        return pane;
    }

    private Pane telaGerirPets(Cliente cliente) {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));

        ObservableList<Pet> pets = FXCollections.observableArrayList(cliente.getPets());
        ListView<Pet> lv = new ListView<>(pets);

        TextField tfNomePet = new TextField();
        TextField tfTipoPet = new TextField();
        TextField tfIdadePet = new TextField();

        Button btnAdd = new Button("Adicionar Pet");
        Button btnRem = new Button("Remover Selecionado");

        btnAdd.setOnAction(e -> {
            String nome = tfNomePet.getText().trim();
            if (nome.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Nome do pet vazio.");
                return;
            }
            int idade = 0;
            try {
                idade = Integer.parseInt(tfIdadePet.getText().trim());
            } catch (Exception ex) { /* ignora */ }
            Pet p = new Pet(nome, tfTipoPet.getText().trim(), idade);
            cliente.adicionarPet(p);
            pets.add(p);
            lv.refresh();
            tfNomePet.clear(); tfTipoPet.clear(); tfIdadePet.clear();
        });

        btnRem.setOnAction(e -> {
            Pet s = lv.getSelectionModel().getSelectedItem();
            if (s != null) {
                cliente.getPets().remove(s);
                pets.remove(s);
            }
        });

        GridPane form = new GridPane();
        form.setVgap(8);
        form.setHgap(8);
        form.add(new Label("Nome:"),0,0); form.add(tfNomePet,1,0);
        form.add(new Label("Tipo:"),0,1); form.add(tfTipoPet,1,1);
        form.add(new Label("Idade:"),0,2); form.add(tfIdadePet,1,2);
        form.add(btnAdd,1,3);
        form.add(btnRem,1,4);

        pane.setLeft(lv);
        pane.setCenter(form);
        return pane;
    }

    /* ---------- SERVIÇOS ---------- */
    private Pane telaServicos() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(12));

        TableView<Servico> tabela = new TableView<>(servicos);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Servico, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        TableColumn<Servico, Double> colPreco = new TableColumn<>("Preço");
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        tabela.getColumns().addAll(colNome, colPreco);

        TextField tfNome = new TextField();
        TextField tfPreco = new TextField();
        Button btnAdd = new Button("Adicionar Serviço");
        Button btnUpdate = new Button("Atualizar");
        Button btnRem = new Button("Remover");

        btnAdd.setOnAction(e -> {
            String n = tfNome.getText().trim();
            double p = parseDouble(tfPreco.getText(), -1);
            if (n.isEmpty() || p <= 0) { mostrarAlerta(Alert.AlertType.WARNING, "Dados inválidos", "Preencha nome e preço válido."); return; }
            servicos.add(new Servico(n, p));
            tfNome.clear(); tfPreco.clear();
        });

        btnUpdate.setOnAction(e -> {
            Servico s = tabela.getSelectionModel().getSelectedItem();
            if (s == null) { mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione serviço."); return; }
            if (!tfNome.getText().trim().isEmpty()) s.setNome(tfNome.getText().trim());
            if (!tfPreco.getText().trim().isEmpty()) {
                double p = parseDouble(tfPreco.getText(), s.getPreco());
                s.setPreco(p);
            }
            tabela.refresh();
        });

        btnRem.setOnAction(e -> {
            Servico s = tabela.getSelectionModel().getSelectedItem();
            if (s != null) servicos.remove(s);
        });

        VBox form = new VBox(8, new Label("Nome:"), tfNome, new Label("Preço:"), tfPreco, btnAdd, btnUpdate, btnRem);
        form.setPadding(new Insets(8));
        pane.setCenter(tabela);
        pane.setRight(form);
        return pane;
    }

    /* ---------- RAÇÕES (PRODUTOS) ---------- */
    private Pane telaRacoes() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(12));

        TableView<Produto> tabela = new TableView<>(racoes);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Produto, String> cNome = new TableColumn<>("Nome");
        cNome.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));
        TableColumn<Produto, String> cDesc = new TableColumn<>("Descrição");
        cDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        TableColumn<Produto, Double> cPreco = new TableColumn<>("Preço (R$/KG)");
        cPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        tabela.getColumns().addAll(cNome, cDesc, cPreco);

        TextField tfNome = new TextField();
        TextField tfDesc = new TextField();
        TextField tfPreco = new TextField();
        Button btnAdd = new Button("Adicionar");
        Button btnUpdate = new Button("Atualizar");
        Button btnRem = new Button("Remover");

        btnAdd.setOnAction(e -> {
            String n = tfNome.getText().trim();
            double p = parseDouble(tfPreco.getText(), -1);
            if (n.isEmpty() || p <= 0) { mostrarAlerta(Alert.AlertType.WARNING, "Dados inválidos", "Preencha nome e preço válido."); return; }
            racoes.add(new Produto(n, tfDesc.getText().trim(), p));
            tfNome.clear(); tfDesc.clear(); tfPreco.clear();
        });

        btnUpdate.setOnAction(e -> {
            Produto pr = tabela.getSelectionModel().getSelectedItem();
            if (pr == null) { mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione produto."); return; }
            if (!tfNome.getText().trim().isEmpty()) pr.setNomeProduto(tfNome.getText().trim());
            if (!tfDesc.getText().trim().isEmpty()) pr.setDescricao(tfDesc.getText().trim());
            if (!tfPreco.getText().trim().isEmpty()) pr.setPreco(parseDouble(tfPreco.getText(), pr.getPreco()));
            tabela.refresh();
        });

        btnRem.setOnAction(e -> {
            Produto pr = tabela.getSelectionModel().getSelectedItem();
            if (pr != null) racoes.remove(pr);
        });

        VBox form = new VBox(8, new Label("Nome:"), tfNome, new Label("Descrição:"), tfDesc, new Label("Preço (R$/KG):"), tfPreco, btnAdd, btnUpdate, btnRem);
        form.setPadding(new Insets(8));
        pane.setCenter(tabela);
        pane.setRight(form);
        return pane;
    }

    /* ---------- FUNCIONÁRIOS ---------- */
    private Pane telaFuncionarios() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(12));

        TableView<Funcionario> tabela = new TableView<>(funcionarios);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Funcionario, String> cNome = new TableColumn<>("Nome");
        cNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Funcionario, String> cFunc = new TableColumn<>("Função");
        cFunc.setCellValueFactory(new PropertyValueFactory<>("funcao"));

        tabela.getColumns().addAll(cNome, cFunc);

        TextField tfNome = new TextField();
        TextField tfFunc = new TextField();
        Button btnAdd = new Button("Adicionar");
        Button btnUpdate = new Button("Atualizar");
        Button btnRem = new Button("Remover");

        btnAdd.setOnAction(e -> {
            String n = tfNome.getText().trim();
            String f = tfFunc.getText().trim();
            if (n.isEmpty() || f.isEmpty()) { mostrarAlerta(Alert.AlertType.WARNING, "Dados inválidos", "Preencha nome e função."); return; }
            funcionarios.add(new Funcionario(n, f));
            tfNome.clear(); tfFunc.clear();
        });

        btnUpdate.setOnAction(e -> {
            Funcionario ff = tabela.getSelectionModel().getSelectedItem();
            if (ff == null) { mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione funcionário."); return; }
            if (!tfNome.getText().trim().isEmpty()) ff.setNome(tfNome.getText().trim());
            if (!tfFunc.getText().trim().isEmpty()) ff.setFuncao(tfFunc.getText().trim());
            tabela.refresh();
        });

        btnRem.setOnAction(e -> {
            Funcionario f = tabela.getSelectionModel().getSelectedItem();
            if (f != null) funcionarios.remove(f);
        });

        VBox form = new VBox(8, new Label("Nome:"), tfNome, new Label("Função:"), tfFunc, btnAdd, btnUpdate, btnRem);
        form.setPadding(new Insets(8));
        pane.setCenter(tabela);
        pane.setRight(form);
        return pane;
    }

    /* ---------- ATENDIMENTO ---------- */
    private Pane telaAtendimento() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(16));

        ComboBox<Cliente> cbCliente = new ComboBox<>(clientes);
        cbCliente.setPrefWidth(300);
        ComboBox<Pet> cbPet = new ComboBox<>();
        cbCliente.setOnAction(e -> {
            Cliente sel = cbCliente.getValue();
            if (sel != null) {
                cbPet.setItems(FXCollections.observableArrayList(sel.getPets()));
            } else cbPet.setItems(FXCollections.emptyObservableList());
        });

        ListView<Servico> lvServicos = new ListView<>(servicos);
        lvServicos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvServicos.setPrefHeight(120);

        // Horários disponíveis para hoje
        ComboBox<String> cbHorario = new ComboBox<>();
        atualizarHorarios(cbHorario);

        // Ração
        ComboBox<Produto> cbRacao = new ComboBox<>(racoes);
        TextField tfKg = new TextField();
        tfKg.setPromptText("Kg");

        // Entrega checkbox
        CheckBox chEntrega = new CheckBox("Entrega (R$ 15,00)");

        // Pagamento
        ComboBox<String> cbPagamento = new ComboBox<>(FXCollections.observableArrayList("Crédito","Débito","Dinheiro","PIX"));
        cbPagamento.getSelectionModel().selectFirst();

        Button btnAgendar = new Button("Agendar Atendimento");
        Button btnGerarRecibo = new Button("Gerar Recibo e Finalizar");

        TextArea taResumo = new TextArea();
        taResumo.setEditable(false);
        taResumo.setPrefHeight(200);

        btnAgendar.setOnAction(e -> {
            Cliente c = cbCliente.getValue();
            if (c == null) { mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione cliente."); return; }
            Pet pet = cbPet.getValue();
            if (pet == null) { mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione ou cadastre um pet no cliente."); return; }
            List<Servico> selecionados = new ArrayList<>(lvServicos.getSelectionModel().getSelectedItems());
            if (selecionados.isEmpty()) { mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione ao menos um serviço."); return; }
            String horario = cbHorario.getValue();
            if (horario == null) { mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione um horário."); return; }
            String dataHoje = LocalDate.now().format(formatoData);

            // verifica conflito
            boolean ocupado = agendamentos.stream()
                    .anyMatch(ag -> ag.getData().equals(dataHoje) && ag.getHorario().equals(horario));
            if (ocupado) { mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Horário já ocupado."); atualizarHorarios(cbHorario); return; }

            Agendamento ag = new Agendamento(c, selecionados, pet, dataHoje, horario);
            agendamentos.add(ag);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Agendado", "Atendimento agendado com sucesso.");
            atualizarHorarios(cbHorario);
        });

        btnGerarRecibo.setOnAction(e -> {
            Cliente c = cbCliente.getValue();
            Pet pet = cbPet.getValue();
            List<Servico> selecionados = new ArrayList<>(lvServicos.getSelectionModel().getSelectedItems());
            if (c == null || pet == null || selecionados.isEmpty()) { mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Cliente, pet e serviços são obrigatórios."); return; }
            String horario = cbHorario.getValue();
            if (horario == null) { mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione horário."); return; }
            String dataHoje = LocalDate.now().format(formatoData);
            // verifica horário novamente
            boolean ocupado = agendamentos.stream()
                    .anyMatch(ag -> ag.getData().equals(dataHoje) && ag.getHorario().equals(horario));
            if (ocupado) { mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Horário já ocupado."); atualizarHorarios(cbHorario); return; }

            // tosador se necessário
            boolean precisaTosador = selecionados.stream().anyMatch(s -> s.getNome().toLowerCase().contains("tosa"));
            Funcionario tosador = null;
            if (precisaTosador) {
                List<Funcionario> fil = funcionarios.stream()
                        .filter(f -> f.getFuncao().toLowerCase().contains("tosador")).collect(Collectors.toList());
                if (!fil.isEmpty()) tosador = fil.get(0);
            }
            Funcionario atendente = funcionarios.stream().filter(f -> f.getFuncao().equalsIgnoreCase("Atendente")).findFirst().orElse(null);

            // ração
            double totalRacoes = 0;
            String itensRacao = "";
            Produto pr = cbRacao.getValue();
            int kg = parseInt(tfKg.getText(), 0);
            if (pr != null && kg > 0) {
                totalRacoes = pr.getPreco() * kg;
                itensRacao = pr.getNomeProduto() + " - " + kg + " KG - R$ " + String.format("%.2f", totalRacoes) + "\n";
            }

            double totalServicos = selecionados.stream().mapToDouble(Servico::getPreco).sum();
            double valorEntrega = chEntrega.isSelected() ? 15.00 : 0.0;
            double totalGeral = totalServicos + totalRacoes + valorEntrega;

            String formaPagamento = cbPagamento.getValue();
            Pagamento pagamento = new Pagamento(formaPagamento, totalGeral);
            String horaAgora = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

            StringBuilder recibo = new StringBuilder();
            recibo.append("Cliente: ").append(c.getNomeCliente()).append("\n");
            recibo.append("Pet: ").append(pet.getNome()).append(" (").append(pet.getTipo()).append(")\n");
            if (atendente != null) recibo.append("Atendente: ").append(atendente.getNome()).append("\n");
            if (precisaTosador && tosador != null) recibo.append("Responsável pela tosa: ").append(tosador.getNome()).append("\n");
            recibo.append("\n--- Serviços ---\n");
            for (Servico s : selecionados) recibo.append(s.getNome()).append(" - R$ ").append(String.format("%.2f", s.getPreco())).append("\n");
            recibo.append("\nData: ").append(dataHoje).append("\n");
            recibo.append("Horário agendado: ").append(horario).append("\n");
            recibo.append("Hora do atendimento: ").append(horaAgora).append("\n\n");
            if (totalRacoes > 0) {
                recibo.append("--- Rações compradas ---\n");
                recibo.append(itensRacao);
                recibo.append("TOTAL EM Rações: R$ ").append(String.format("%.2f", totalRacoes)).append("\n\n");
            }
            if (chEntrega.isSelected()) recibo.append("Serviço de entrega: R$ ").append(String.format("%.2f", valorEntrega)).append("\n");
            recibo.append("TOTAL DOS SERVIÇOS: R$ ").append(String.format("%.2f", totalServicos)).append("\n\n");
            recibo.append(pagamento.gerarResumo()).append("\n");
            recibo.append("===== TOTAL DO ATENDIMENTO: R$ ").append(String.format("%.2f", totalGeral)).append(" =====\n\n");
            recibo.append("===== FIM DO ATENDIMENTO =====\n");

            // salva agendamento também
            Agendamento ag = new Agendamento(c, selecionados, pet, dataHoje, horario);
            agendamentos.add(ag);

            taResumo.setText(recibo.toString());
            recibos.add(recibo.toString());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Atendimento", "Recibo gerado e atendimento finalizado.");
            atualizarHorarios(cbHorario);
        });

        // layout
        grid.add(new Label("Cliente:"), 0, 0);
        grid.add(cbCliente, 1, 0);
        grid.add(new Label("Pet:"), 0, 1);
        grid.add(cbPet, 1, 1);
        grid.add(new Label("Serviços (CTRL+click múltiplos):"), 0, 2);
        grid.add(lvServicos, 1, 2);
        grid.add(new Label("Horário:"), 0, 3);
        grid.add(cbHorario, 1, 3);
        grid.add(new Label("Ração (opcional):"), 0, 4);
        HBox hRacao = new HBox(8, cbRacao, tfKg);
        grid.add(hRacao, 1, 4);
        grid.add(chEntrega, 1, 5);
        grid.add(new Label("Pagamento:"), 0, 6);
        grid.add(cbPagamento, 1, 6);
        grid.add(btnAgendar, 0, 7);
        grid.add(btnGerarRecibo, 1, 7);
        grid.add(new Label("Recibo / Resumo:"), 0, 8);
        grid.add(taResumo, 0, 9, 2, 1);

        return grid;
    }

    private void atualizarHorarios(ComboBox<String> cbHorario) {
        String dataHoje = LocalDate.now().format(formatoData);
        List<String> disponiveis = new ArrayList<>();
        for (String h : HORARIOS_DISPONIVEIS) {
            boolean ocupado = agendamentos.stream().anyMatch(ag -> ag.getData().equals(dataHoje) && ag.getHorario().equals(h));
            if (!ocupado) disponiveis.add(h);
        }
        cbHorario.setItems(FXCollections.observableArrayList(disponiveis));
        if (!disponiveis.isEmpty()) cbHorario.getSelectionModel().selectFirst();
    }

    /* ---------- AGENDAMENTOS ---------- */
    private Pane telaAgendamentos() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(12));

        TableView<Agendamento> tabela = new TableView<>(agendamentos);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Agendamento, String> c1 = new TableColumn<>("Cliente");
        c1.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getCliente().getNomeCliente()));
        TableColumn<Agendamento, String> c2 = new TableColumn<>("Pet");
        c2.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getPet().getNome()));
        TableColumn<Agendamento, String> c3 = new TableColumn<>("Data");
        c3.setCellValueFactory(new PropertyValueFactory<>("data"));
        TableColumn<Agendamento, String> c4 = new TableColumn<>("Horário");
        c4.setCellValueFactory(new PropertyValueFactory<>("horario"));
        TableColumn<Agendamento, String> c5 = new TableColumn<>("Serviços");
        c5.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().getServicos().stream().map(Servico::getNome).collect(Collectors.joining(", "))));

        tabela.getColumns().addAll(c1,c2,c3,c4,c5);

        box.getChildren().addAll(criarTitulo("Agendamentos"), tabela);
        return box;
    }

    /* ---------- RECIBOS ---------- */
    private Pane telaRecibos() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(12));
        ListView<String> lv = new ListView<>(recibos);
        lv.setPrefHeight(400);

        Button btnSalvar = new Button("Exportar Recibo Selecionado");
        btnSalvar.setOnAction(e -> {
            String selecionado = lv.getSelectionModel().getSelectedItem();
            if (selecionado == null) { mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione um recibo."); return; }
            FileChooser fc = new FileChooser();
            fc.setInitialFileName("recibo.txt");
            File f = fc.showSaveDialog(stage);
            if (f != null) {
                try (FileWriter fw = new FileWriter(f)) {
                    fw.write(selecionado);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Recibo salvo em " + f.getAbsolutePath());
                } catch (Exception ex) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível salvar o arquivo.");
                }
            }
        });

        box.getChildren().addAll(criarTitulo("Recibos"), lv, btnSalvar);
        return box;
    }

    /* ------------------ UTILITÁRIOS ------------------ */
    private void mostrarAlerta(Alert.AlertType type, String titulo, String msg) {
        Alert a = new Alert(type);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private static void limparCampos(TextField... tfs) { for (TextField t: tfs) t.clear(); }

    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception ex) { return def; }
    }

    private static double parseDouble(String s, double def) {
        try { return Double.parseDouble(s.trim().replace(",", ".")); } catch (Exception ex) { return def; }
    }

    /* ---------- Console-compatible function (mantive para compatibilidade) ---------- */
    private void gerarRecibosAoSair() {
        System.out.println("\n===== RECIBOS GERADOS =====");
        if (recibos.isEmpty()) {
            System.out.println("Nenhum recibo gerado.");
            return;
        }
        for (String r : recibos) System.out.println(r);
    }

    // ---- Tema: alternar / aplicar ----
    private void alternarTema() {
        temaAzul = !temaAzul;
        Scene s = stage.getScene();
        if (s != null) aplicarTema(s);
    }

    private void aplicarTema(Scene scene) {
        if (scene == null) return;
        // Remove ambas classes e adiciona a correta
        scene.getRoot().getStyleClass().removeAll("tema-azul", "tema-laranja");
        if (temaAzul) {
            scene.getRoot().getStyleClass().add("tema-azul");
        } else {
            scene.getRoot().getStyleClass().add("tema-laranja");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
