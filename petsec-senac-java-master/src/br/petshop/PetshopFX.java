package br.petshop;



import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Optional;

public class PetshopFX extends Application {

    private PetShop shop = new PetShop();

    // UI elements used across methods
    private ListView<String> clientesList = new ListView<>();
    private ListView<String> servicosList = new ListView<>();
    private ListView<String> racoesList = new ListView<>();
    private ListView<String> agendamentosList = new ListView<>();
    private ListView<String> comprasList = new ListView<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        inicializarDadosPadrao();

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // Top bar
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // Left menu
        VBox leftMenu = createLeftMenu();
        root.setLeft(leftMenu);

        // Center content: tabs
        TabPane centerTabs = createCenterTabs();
        root.setCenter(centerTabs);

        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(getClass().getResource("petshop.css").toExternalForm());

        primaryStage.setTitle("Petshop - Painel");
        primaryStage.setScene(scene);
        primaryStage.show();

        // initial refresh
        refreshAll();
    }

    private HBox createTopBar() {
        HBox top = new HBox();
        top.setPadding(new Insets(14));
        top.setSpacing(12);
        top.setAlignment(Pos.CENTER_LEFT);
        top.getStyleClass().add("topbar");

        // small logo (use emoji if you don't have an image)
        Label logo = new Label("🐶 PETSHOP");
        logo.getStyleClass().add("logo");
        logo.setFont(Font.font(20));

        Button btnRecibos = new Button("Gerar Recibos");
        btnRecibos.getStyleClass().add("accent");
        btnRecibos.setOnAction(e -> {
            shop.gerarRecibos();
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Recibos gerados (ver console / implementação).");
            a.setHeaderText("OK");
            a.showAndWait();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        top.getChildren().addAll(logo, spacer, btnRecibos);
        return top;
    }

    private VBox createLeftMenu() {
        VBox menu = new VBox();
        menu.setPrefWidth(260);
        menu.setPadding(new Insets(18));
        menu.setSpacing(12);
        menu.getStyleClass().add("leftmenu");

        Label title = new Label("Ações rápidas");
        title.getStyleClass().add("left-title");

        Button btnAddCliente = new Button("Cadastrar Cliente");
        btnAddCliente.setMaxWidth(Double.MAX_VALUE);
        btnAddCliente.setOnAction(e -> mostrarDialogoCadastrarCliente());

        Button btnAddServico = new Button("Cadastrar Serviço");
        btnAddServico.setMaxWidth(Double.MAX_VALUE);
        btnAddServico.setOnAction(e -> mostrarDialogoCadastrarServico());

        Button btnAddRacao = new Button("Cadastrar Ração");
        btnAddRacao.setMaxWidth(Double.MAX_VALUE);
        btnAddRacao.setOnAction(e -> mostrarDialogoCadastrarRacao());

        Button btnAgendar = new Button("Agendar Serviço");
        btnAgendar.setMaxWidth(Double.MAX_VALUE);
        btnAgendar.setOnAction(e -> mostrarDialogoAgendarServico());

        Button btnComprar = new Button("Comprar Ração");
        btnComprar.setMaxWidth(Double.MAX_VALUE);
        btnComprar.setOnAction(e -> mostrarDialogoComprarRacao());

        menu.getChildren().addAll(title, btnAddCliente, btnAddServico, btnAddRacao, new Separator(), btnAgendar, btnComprar);
        return menu;
    }

    private TabPane createCenterTabs() {
        TabPane tabs = new TabPane();

        Tab tClientes = new Tab("Clientes");
        VBox vClientes = new VBox(10, clientesList);
        vClientes.setPadding(new Insets(12));
        tClientes.setContent(vClientes);
        tClientes.setClosable(false);

        Tab tServicos = new Tab("Serviços");
        VBox vServicos = new VBox(10, servicosList);
        vServicos.setPadding(new Insets(12));
        tServicos.setContent(vServicos);
        tServicos.setClosable(false);

        Tab tRacoes = new Tab("Rações");
        VBox vRacoes = new VBox(10, racoesList);
        vRacoes.setPadding(new Insets(12));
        tRacoes.setContent(vRacoes);
        tRacoes.setClosable(false);

        Tab tAgendamentos = new Tab("Agendamentos");
        VBox vAg = new VBox(10, agendamentosList);
        vAg.setPadding(new Insets(12));
        tAgendamentos.setContent(vAg);
        tAgendamentos.setClosable(false);

        Tab tCompras = new Tab("Compras");
        VBox vComp = new VBox(10, comprasList);
        vComp.setPadding(new Insets(12));
        tCompras.setContent(vComp);
        tCompras.setClosable(false);

        tabs.getTabs().addAll(tClientes, tServicos, tRacoes, tAgendamentos, tCompras);
        return tabs;
    }

    private void refreshAll() {
        refreshClientes();
        refreshServicos();
        refreshRacoes();
        refreshAgendamentos();
        refreshCompras();
    }

    private void refreshClientes() {
        clientesList.getItems().clear();
        if (shop.getClientes().isEmpty()) {
            clientesList.getItems().add("Nenhum cliente cadastrado");
            return;
        }
        for (Cliente c : shop.getClientes()) {
            StringBuilder sb = new StringBuilder();
            sb.append(c.getNome()).append(" - ").append(c.getTelefone());
            if (!c.getAnimais().isEmpty()) {
                sb.append("\n   Animais:");
                for (Animal a : c.getAnimais()) {
                    sb.append("\n     • ").append(a.getNome()).append(" (").append(a.getEspecie()).append(", ").append(a.getIdade()).append(" anos)");
                }
            }
            clientesList.getItems().add(sb.toString());
        }
    }

    private void refreshServicos() {
        servicosList.getItems().clear();
        if (shop.getServicos().isEmpty()) {
            servicosList.getItems().add("Nenhum serviço cadastrado");
            return;
        }
        for (Servico s : shop.getServicos()) {
            servicosList.getItems().add(s.getNome() + " — R$ " + String.format("%.2f", s.getPreco()) + " | Horários: " + s.getHorariosDisponiveis());
        }
    }

    private void refreshRacoes() {
        racoesList.getItems().clear();
        if (shop.getRacoes().isEmpty()) {
            racoesList.getItems().add("Nenhuma ração cadastrada");
            return;
        }
        for (Racao r : shop.getRacoes()) {
            racoesList.getItems().add(r.getNome() + " — R$ " + String.format("%.2f", r.getPreco()) + " | Estoque: " + r.getQuantidade());
        }
    }

    private void refreshAgendamentos() {
        agendamentosList.getItems().clear();
        if (shop.getAgendamentos().isEmpty()) {
            agendamentosList.getItems().add("Nenhum agendamento");
            return;
        }
        for (Agendamento a : shop.getAgendamentos()) {
            agendamentosList.getItems().add(a.getHorario() + " — " + a.getServico().getNome() + " | Cliente: " + a.getCliente().getNome() + " / " + a.getAnimal().getNome());
        }
    }

    private void refreshCompras() {
        comprasList.getItems().clear();
        if (shop.getCompras().isEmpty()) {
            comprasList.getItems().add("Nenhuma compra");
            return;
        }
        for (Compra c : shop.getCompras()) {
            comprasList.getItems().add(c.getQuantidade() + " x " + c.getRacao().getNome() + " — Cliente: " + c.getCliente().getNome() + " | Total: R$ " + String.format("%.2f", c.getQuantidade() * c.getRacao().getPreco()));
        }
    }

    // ---------- DIALOGS / ACTIONS ----------

    private void mostrarDialogoCadastrarCliente() {
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle("Cadastrar Cliente");

        ButtonType salvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(8);
        grid.setPadding(new Insets(12));

        TextField nomeField = new TextField();
        TextField telField = new TextField();

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Telefone:"), 0, 1);
        grid.add(telField, 1, 1);

        // animal quick-add
        TextField animalNome = new TextField();
        TextField animalEspecie = new TextField();
        TextField animalIdade = new TextField();

        grid.add(new Label("Nome do animal (opcional):"), 0, 2);
        grid.add(animalNome, 1, 2);
        grid.add(new Label("Espécie:"), 0, 3);
        grid.add(animalEspecie, 1, 3);
        grid.add(new Label("Idade:"), 0, 4);
        grid.add(animalIdade, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == salvar) {
                String nome = nomeField.getText().trim();
                String tel = telField.getText().trim();
                if (nome.isEmpty()) {
                    Alert a = new Alert(Alert.AlertType.WARNING, "Nome é obrigatório.");
                    a.showAndWait();
                    return null;
                }
                Cliente c = new Cliente(nome, tel);
                if (!animalNome.getText().trim().isEmpty()) {
                    int idade = 0;
                    try {
                        idade = Integer.parseInt(animalIdade.getText().trim());
                    } catch (Exception ex) { /* ignore */ }
                    Animal a = new Animal(animalNome.getText().trim(), animalEspecie.getText().trim(), idade);
                    c.adicionarAnimal(a);
                }
                return c;
            }
            return null;
        });

        Optional<Cliente> result = dialog.showAndWait();
        result.ifPresent(c -> {
            shop.adicionarCliente(c);
            refreshClientes();
        });
    }

    private void mostrarDialogoCadastrarServico() {
        Dialog<Servico> dialog = new Dialog<>();
        dialog.setTitle("Cadastrar Serviço");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(8);
        grid.setPadding(new Insets(12));

        TextField nome = new TextField();
        TextField preco = new TextField();
        TextField horarios = new TextField(); // comma-separated

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nome, 1, 0);
        grid.add(new Label("Preço (ex: 50.00):"), 0, 1);
        grid.add(preco, 1, 1);
        grid.add(new Label("Horários (separados por vírgula):"), 0, 2);
        grid.add(horarios, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String n = nome.getText().trim();
                double p = 0;
                try { p = Double.parseDouble(preco.getText().trim()); } catch (Exception e) {}
                Servico s = new Servico(n, p);
                String[] hs = horarios.getText().split(",");
                for (String h : hs) {
                    if (!h.trim().isEmpty()) s.adicionarHorario(h.trim());
                }
                return s;
            }
            return null;
        });

        Optional<Servico> res = dialog.showAndWait();
        res.ifPresent(s -> {
            shop.adicionarServico(s);
            refreshServicos();
        });
    }

    private void mostrarDialogoCadastrarRacao() {
        Dialog<Racao> dialog = new Dialog<>();
        dialog.setTitle("Cadastrar Ração");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(8);
        grid.setPadding(new Insets(12));

        TextField nome = new TextField();
        TextField preco = new TextField();
        TextField qtd = new TextField();

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nome, 1, 0);
        grid.add(new Label("Preço:"), 0, 1);
        grid.add(preco, 1, 1);
        grid.add(new Label("Quantidade em estoque:"), 0, 2);
        grid.add(qtd, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String n = nome.getText().trim();
                double p = 0;
                int q = 0;
                try { p = Double.parseDouble(preco.getText().trim()); } catch (Exception e) {}
                try { q = Integer.parseInt(qtd.getText().trim()); } catch (Exception e) {}
                return new Racao(n, p, q);
            }
            return null;
        });

        Optional<Racao> res = dialog.showAndWait();
        res.ifPresent(r -> {
            shop.adicionarRacao(r);
            refreshRacoes();
        });
    }

    private void mostrarDialogoAgendarServico() {
        if (shop.getClientes().isEmpty() || shop.getServicos().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING, "É preciso ter pelo menos 1 cliente e 1 serviço cadastrado.");
            a.showAndWait();
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Agendar Serviço");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(12));
        grid.setVgap(8);
        grid.setHgap(8);

        ComboBox<String> cbCliente = new ComboBox<>();
        for (Cliente c : shop.getClientes()) cbCliente.getItems().add(c.getNome());
        cbCliente.getSelectionModel().selectFirst();

        ComboBox<String> cbAnimal = new ComboBox<>();
        // populate when cliente changes
        cbCliente.valueProperty().addListener((obs, oldV, newV) -> {
            cbAnimal.getItems().clear();
            Cliente chosen = buscarClientePorNome(newV);
            if (chosen != null) {
                for (Animal a : chosen.getAnimais()) cbAnimal.getItems().add(a.getNome());
                if (!cbAnimal.getItems().isEmpty()) cbAnimal.getSelectionModel().selectFirst();
            }
        });

        // initial animal fill
        Cliente initial = shop.getClientes().get(0);
        for (Animal a : initial.getAnimais()) cbAnimal.getItems().add(a.getNome());

        ComboBox<String> cbServico = new ComboBox<>();
        for (Servico s : shop.getServicos()) cbServico.getItems().add(s.getNome());
        cbServico.getSelectionModel().selectFirst();

        ComboBox<String> cbHorario = new ComboBox<>();
        // update horarios when service changes
        cbServico.valueProperty().addListener((obs, oldV, newV) -> {
            cbHorario.getItems().clear();
            Servico serv = buscarServicoPorNome(newV);
            if (serv != null) {
                cbHorario.getItems().addAll(serv.getHorariosDisponiveis());
                if (!cbHorario.getItems().isEmpty()) cbHorario.getSelectionModel().selectFirst();
            }
        });
        // initial fill
        if (!shop.getServicos().isEmpty()) {
            Servico s0 = shop.getServicos().get(0);
            cbHorario.getItems().addAll(s0.getHorariosDisponiveis());
            if (!cbHorario.getItems().isEmpty()) cbHorario.getSelectionModel().selectFirst();
        }

        grid.add(new Label("Cliente:"), 0, 0);
        grid.add(cbCliente, 1, 0);
        grid.add(new Label("Animal:"), 0, 1);
        grid.add(cbAnimal, 1, 1);
        grid.add(new Label("Serviço:"), 0, 2);
        grid.add(cbServico, 1, 2);
        grid.add(new Label("Horário:"), 0, 3);
        grid.add(cbHorario, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            Cliente c = buscarClientePorNome(cbCliente.getValue());
            Animal a = (c != null) ? buscarAnimalPorNome(c, cbAnimal.getValue()) : null;
            Servico s = buscarServicoPorNome(cbServico.getValue());
            String horario = cbHorario.getValue();

            if (c == null || a == null || s == null || horario == null) {
                Alert at = new Alert(Alert.AlertType.ERROR, "Dados inválidos para agendamento.");
                at.showAndWait();
                return;
            }

            if (shop.horarioJaAgendado(s, horario)) {
                Alert at = new Alert(Alert.AlertType.WARNING, "⚠️ Esse horário já foi agendado!");
                at.showAndWait();
                return;
            }

            Agendamento ag = new Agendamento(c, a, s, horario);
            shop.adicionarAgendamento(ag);
            refreshAgendamentos();
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Agendamento realizado com sucesso!");
            ok.showAndWait();
        }
    }

    private void mostrarDialogoComprarRacao() {
        if (shop.getClientes().isEmpty() || shop.getRacoes().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING, "É preciso ter pelo menos 1 cliente e 1 ração cadastrado.");
            a.showAndWait();
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Comprar Ração");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(12));
        grid.setVgap(8);
        grid.setHgap(8);

        ComboBox<String> cbCliente = new ComboBox<>();
        for (Cliente c : shop.getClientes()) cbCliente.getItems().add(c.getNome());
        cbCliente.getSelectionModel().selectFirst();

        ComboBox<String> cbRacao = new ComboBox<>();
        for (Racao r : shop.getRacoes()) cbRacao.getItems().add(r.getNome() + " (estoque: " + r.getQuantidade() + ")");
        cbRacao.getSelectionModel().selectFirst();

        TextField qtdField = new TextField("1");

        grid.add(new Label("Cliente:"), 0, 0);
        grid.add(cbCliente, 1, 0);
        grid.add(new Label("Ração:"), 0, 1);
        grid.add(cbRacao, 1, 1);
        grid.add(new Label("Quantidade:"), 0, 2);
        grid.add(qtdField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            Cliente c = buscarClientePorNome(cbCliente.getValue());
            // map selection back to Racao by name (safe because we used r.getNome())
            String selectedRacaoNome = cbRacao.getValue();
            // extract name before " (estoque"
            String nomeRacao = selectedRacaoNome.split(" \\(estoque")[0];

            Racao r = buscarRacaoPorNome(nomeRacao);
            int qtd = 1;
            try { qtd = Integer.parseInt(qtdField.getText().trim()); } catch (Exception ex) {}

            if (r == null || c == null) {
                Alert at = new Alert(Alert.AlertType.ERROR, "Dados inválidos.");
                at.showAndWait();
                return;
            }

            if (qtd > r.getQuantidade()) {
                Alert at = new Alert(Alert.AlertType.WARNING, "Quantidade insuficiente em estoque!");
                at.showAndWait();
                return;
            }

            r.setQuantidade(r.getQuantidade() - qtd);
            Compra purchase = new Compra(c, r, qtd);
            shop.adicionarCompra(purchase);
            refreshRacoes();
            refreshCompras();
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Compra registrada com sucesso!");
            ok.showAndWait();
        }
    }

    // ---------- Helpers to find domain objects by name ----------
    private Cliente buscarClientePorNome(String nome) {
        for (Cliente c : shop.getClientes()) if (c.getNome().equals(nome)) return c;
        return null;
    }

    private Animal buscarAnimalPorNome(Cliente c, String nome) {
        if (c == null) return null;
        for (Animal a : c.getAnimais()) if (a.getNome().equals(nome)) return a;
        return null;
    }

    private Servico buscarServicoPorNome(String nome) {
        for (Servico s : shop.getServicos()) if (s.getNome().equals(nome)) return s;
        return null;
    }

    private Racao buscarRacaoPorNome(String nome) {
        for (Racao r : shop.getRacoes()) if (r.getNome().equals(nome)) return r;
        return null;
    }

    // ---------- Inicializa com os mesmos padrões do seu Main console ----------
    private void inicializarDadosPadrao() {
        Servico banho = new Servico("Banho", 50);
        banho.adicionarHorario("09:00");
        banho.adicionarHorario("10:00");
        banho.adicionarHorario("11:00");

        Servico tosa = new Servico("Tosa", 80);
        tosa.adicionarHorario("14:00");
        tosa.adicionarHorario("16:00");

        shop.adicionarServico(banho);
        shop.adicionarServico(tosa);

        Racao r1 = new Racao("Ração Popular 10kg - Cães Adultos", 59.90, 20);
        Racao r2 = new Racao("Ração Econômica 5kg - Gatos", 42.50, 15);
        Racao r3 = new Racao("Ração Premium 10kg - Cães", 119.90, 10);
        Racao r4 = new Racao("Ração Super Premium 7kg - Gatos", 159.90, 8);
        Racao r5 = new Racao("Ração Natural & Grain Free 3kg - Cães Pequenos", 189.50, 5);

        shop.adicionarRacao(r1);
        shop.adicionarRacao(r2);
        shop.adicionarRacao(r3);
        shop.adicionarRacao(r4);
        shop.adicionarRacao(r5);

        // opcional: adicionar cliente demo
        Cliente demo = new Cliente("Gabriel Exemplo", "99999-9999");
        demo.adicionarAnimal(new Animal("Rex", "Cão", 3));
        shop.adicionarCliente(demo);
    }
}
