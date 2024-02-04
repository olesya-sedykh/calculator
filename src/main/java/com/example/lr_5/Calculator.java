package com.example.lr_5;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EmptyStackException;
import java.util.Stack;

public class Calculator extends Application {
    //объявление полей класса
    private Button buttonSin;
    private Button buttonOpeningBracket;
    private Button buttonClosingBracket;
    private Button buttonC;
    private Button buttonX;

    private Button buttonCos;
    private Button buttonSqr;
    private Button buttonSqrt;
    private Button buttonN;
    private Button buttonDivision;

    private Button buttonTg;
    private Button button7;
    private Button button8;
    private Button button9;
    private Button buttonMultiplication;

    private Button buttonCtg;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button buttonSubtraction;

    private Button buttonLn;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button buttonSum;

    private Button buttonLg;
    private Button buttonE;
    private Button button0;
    private Button buttonComma;
    private Button buttonResult;

    private TextField textField;

    private GridPane gridPane;
    private GridPane buttons;

    private boolean flag = true;

    //функция вывода сообщений об ошибках
    public void message(String error) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        if (error == "incorrect") { alert.setContentText("Некорректное выражение"); }
        else if (error == "divisionByZero") { alert.setContentText("Некорректное выражение. Попытка деления на 0"); }
        else if (error == "tg") { alert.setContentText("Некорректное выражение. Тангенс угла (90 + pi*n) градусов не существует"); }
        else if (error == "ctg") { alert.setContentText("Некорректное выражение. Котангенс угла (pi*n) градусов не существует"); }
        else if (error == "log") { alert.setContentText("Некорректное выражение. Число под логарифмом должно быть > 0"); }
        else if (error == "sqrt") { alert.setContentText("Некорректное выражение. Число под корнем должно быть >= 0"); }

        alert.showAndWait();
        textField.clear();
    }

    //проверка корректности первоначальной строки
    public void checkCorrectness (String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ',') {
                if (i > 0 && s.length() > i + 1) {
                    if (s.charAt(i - 1) >= '0' && s.charAt(i - 1) <= '9' && s.charAt(i + 1) >= '0' && s.charAt(i + 1) <= '9') {}
                    else { message("incorrect"); }
                    break;
                }
                else { message("incorrect"); }
                break;
            }
            if (s.charAt(i) == '(') { //new '√'  || s.charAt(i) == '√'
                if (i > 0) {
                    if ((s.charAt(i - 1) >= '0' && s.charAt(i - 1) <= '9') || (s.charAt(i - 1) == ',')) {
                        message("incorrect");
                        break;
                    }
                }
                if (s.length() > i + 1) {
                    if (s.charAt(i + 1) == '+' || s.charAt(i + 1) == '-' || s.charAt(i + 1) == '*' ||
                            s.charAt(i + 1) == '/' || s.charAt(i + 1) == '^' || s.charAt(i + 1) == '√')
                    {
                        message("incorrect");
                        break;
                    }
                }
            }
            if ((s.length() - s.replace("(", "").length()) != (s.length() - s.replace(")", "").length())) {
                message("incorrect");
                break;
            }
            //new
//            if (s.charAt(i) == '+' || s.charAt(i) == '-' || s.charAt(i) == '*' || s.charAt(i) == '/') {
//                if (i > 0) {
//                    if (s.length() > i + 1) {
//                        if (s.charAt(i - 1) != ')' || s.charAt(i - 1) <= '0' || s.charAt(i - 1) >= '9' ||
//                                s.charAt(i + 1) != '(' || s.charAt(i + 1) <= '0' || s.charAt(i + 1) >= '9') {
//                            message("incorrect");
//                            break;
//                        }
//                    }
//                    else {
//                        message("incorrect");
//                        break;
//                    }
//                }
//                else {
//                    message("incorrect");
//                    break;
//                }
//            }
            if (s.charAt(i) == 's' || s.charAt(i) == 'c' || s.charAt(i) == 't' || s.charAt(i) == 'h' ||
                    s.charAt(i) == 'n' || s.charAt(i) == 'g' || s.charAt(i) == '√') {
                if (i > 0) {
                    if (s.charAt(i - 1) != '+' && s.charAt(i - 1) != '-' && s.charAt(i - 1) != '*' &&
                            s.charAt(i - 1) != '/' && s.charAt(i - 1) != '^' && s.charAt(i - 1) != '√' &&
                                s.charAt(i - 1) != '(') {
                        message("incorrect");
                        break;
                    }
                }
            }
        }
    }

    //преобразование строки с выражением в более удобную
    //для дальнейшей обработки форму
    public String transform1 (String s) {
        s = s.replace("sin", "s");
        s = s.replace("cos", "c");
        s = s.replace("ctg", "h");
        s = s.replace("tg", "t");
        s = s.replace("ln", "n");
        s = s.replace("lg", "g");
        s = s.replace("exp", "e");
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '-') {
                if (i == 0 || s.charAt(i - 1) == '(' || s.charAt(i - 1) == '√') {
                    s = s.substring(0, i) + 'm' + s.substring(i + 1, s.length());
                }
            }
        }
        return s;
    }

    //преобразование в обратную польскую нотацию
    public String transform2(String start) {
        String end = "";
        Stack<Character> stack = new Stack<Character>();
        for (int i = 0; i < start.length(); i++) {
            if (((start.charAt(i) >= '0') && (start.charAt(i) <= '9')) || (start.charAt(i) == ',')) {
                end += start.charAt(i);
            }
            else if (start.charAt(i) == '(') {
                stack.push(start.charAt(i));
                end += ' ';
            }
            else if (start.charAt(i) == '+' || start.charAt(i) == '-') {
                if (!stack.empty()) {
                    if (stack.peek() == '(') {
                        stack.push(start.charAt(i));
                    }
                    else {
                        while (!stack.empty()) {
                            if (stack.peek() == '(') {
                                break;
                            }
                            end += stack.pop();
                        }
                        stack.push(start.charAt(i));
                    }
                }
                else {
                    stack.push(start.charAt(i));
                }
                end += ' ';
            }
            else if (start.charAt(i) == '*' || start.charAt(i) == '/') {
                if (!stack.empty()) {
                    if (stack.peek() == '(' || stack.peek() == '+' || stack.peek() == '-') {
                        stack.push(start.charAt(i));
                    }
                    else {
                        while (!stack.empty())  {
                            if (stack.peek() == '(' || stack.peek() == '+' || stack.peek() != '-') {
                                break;
                            }
                            end += stack.pop();
                        }
                        stack.push(start.charAt(i));
                    }
                }
                else {
                    stack.push(start.charAt(i));
                }
                end += ' ';
            }
            else if (start.charAt(i) == 's' || start.charAt(i) == 'c' || start.charAt(i) == 't' || start.charAt(i) == 'h' || start.charAt(i) == 'n' || start.charAt(i) == 'g' || start.charAt(i) == 'm' || start.charAt(i) == 'e' || start.charAt(i) == '^' || start.charAt(i) == '√') {
                if (!stack.empty()) {
                    if (stack.peek() == '(' || stack.peek() == '+' || stack.peek() == '-' || stack.peek() == '*' || stack.peek() == '/') {
                        stack.push(start.charAt(i));
                    }
                    else {
                        while (!stack.empty()) {
                            if (stack.peek() == '(' || stack.peek() == '+' || stack.peek() == '-' || stack.peek() != '*' || stack.peek() != '/') {
                                break;
                            }
                            end += stack.pop();
                        }
                        stack.push(start.charAt(i));
                    }
                }
                else {
                    stack.push(start.charAt(i));
                }
                end += ' ';
            }
            else if (start.charAt(i) == ')') {
                while (stack.peek() != '(') {
                    end += stack.pop();
                }
                stack.pop();
                end += ' ';
            }
        }
        while (!stack.empty()) {
            end += stack.pop();
        }
        return end;
    }

    //вычисление выражения
    public String calculate(String s) throws EmptyStackException {
        String result = Character.toString(s.charAt(0));
        Stack<String> stack = new Stack<String>();
        String t = "";
        try {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) >= '0' && s.charAt(i) <= '9') {
                    t += s.charAt(i);
                }
                else if (s.charAt(i) == ',') {
                    t += '.';
                }
                else if (s.charAt(i) == ' ') {
                    if (t != "") {
                        stack.push(t);
                        t = "";
                    }
                }
                else if (s.charAt(i) == '+') {
                    if (t != "") stack.push(t);
                    t = "";
                    double operand1 = Double.parseDouble(stack.pop());
                    double operand2 = Double.parseDouble(stack.pop());
                    result = Double.toString(operand1 + operand2);
                    stack.push(result);
                }
                else if (s.charAt(i) == '-') {
                    if (t != "") stack.push(t);
                    t = "";
                    double operand2 = Double.parseDouble(stack.pop());
                    double operand1 = Double.parseDouble(stack.pop());
                    result = Double.toString(operand1 - operand2);
                    stack.push(result);
                }
                else if (s.charAt(i) == '*') {
                    if (t != "") stack.push(t);
                    t = "";
                    double operand1 = Double.parseDouble(stack.pop());
                    double operand2 = Double.parseDouble(stack.pop());
                    result = Double.toString(operand1 * operand2);
                    stack.push(result);
                }
                else if (s.charAt(i) == '/') {
                    if (t != "") stack.push(t);
                    t = "";
                    double operand2 = Double.parseDouble(stack.pop());
                    if (operand2 == 0) { message("divisionByZero"); break;}
                    double operand1 = Double.parseDouble(stack.pop());
                    result = Double.toString(operand1 / operand2);
                    stack.push(result);
                }
                else if (s.charAt(i) == 's') {
                    if (t != "") stack.push(t);
                    t = "";
                    double operand = Double.parseDouble(stack.pop());
                    //result = String.format("%.5f",Math.sin(Math.PI * operand / 180));
                    result = Double.toString(Math.sin(Math.PI * operand / 180));
                    stack.push(result);
                }
                else if (s.charAt(i) == 'c') {
                    if (t != "") stack.push(t);
                    t = "";
                    double operand = Double.parseDouble(stack.pop());
                    result = Double.toString(Math.cos(Math.PI * operand / 180));
                    stack.push(result);
                }
                else if (s.charAt(i) == 't') {
                    if (t != "") stack.push(t);
                    t = "";
                    double operand = Double.parseDouble(stack.pop());
                    if (operand != 0 && operand % 90 == 0) { message("tg"); break;}
                    result = Double.toString(Math.tan(Math.PI * operand / 180));
                    stack.push(result);
                }
                else if (s.charAt(i) == 'h') {
                    if (t != "") stack.push(t);
                    t = "";
                    double operand = Double.parseDouble(stack.pop());
                    if (operand == 0 || operand % 180 == 0) { message("ctg"); break;}
                    result = Double.toString(1 / Math.tan(Math.PI * operand / 180));
                    stack.push(result);
                }
                else if (s.charAt(i) == 'n') {
                    if (t != "") stack.push(t);
                    t = "";
                    double operand = Double.parseDouble(stack.pop());
                    if (operand <= 0) { message("log"); break;}
                    result = Double.toString(Math.log(operand));
                    stack.push(result);
                }
                else if (s.charAt(i) == 'g') {
                    if (t != "") stack.push(t);
                    t = "";
                    double operand = Double.parseDouble(stack.pop());
                    if (operand <= 0) { message("log"); break;}
                    result = Double.toString(Math.log10(operand));
                    stack.push(result);
                }
                else if (s.charAt(i) == '^') {
                    if (t != "") stack.push(t);
                    t = "";
                    double operand2 = Double.parseDouble(stack.pop());
                    double operand1 = Double.parseDouble(stack.pop());
                    result = Double.toString(Math.pow(operand1, operand2));
                    stack.push(result);
                }
                else if (s.charAt(i) == '√') {
                    if (t != "") stack.push(t);
                    t = "";
                    double operand = Double.parseDouble(stack.pop());
                    if (operand < 0) { message("sqrt"); break;}
                    result = Double.toString(Math.sqrt(operand));
                    stack.push(result);
                }
                else if (s.charAt(i) == 'm') {
                    if (t != "") stack.push(t);
                    t = "";
                    double operand = Double.parseDouble(stack.pop());
                    result = Double.toString(operand * (-1));
                    stack.push(result);
                }
                else if (s.charAt(i) == 'e') {
                    if (t != "") stack.push(t);
                    t = "";
                    double operand = Double.parseDouble(stack.pop());
                    result = Double.toString(Math.exp(operand));
                    stack.push(result);
                }
            }
        }
        catch (EmptyStackException e) {
            message("incorrect");
        }
        return result;
    }

    //функция нажатия на кнопки
    public void click(Button button) {
        String answer = "";
        if (button == buttonSin || button == buttonCos || button == buttonTg || button == buttonCtg || button == buttonLn || button == buttonLg || button == buttonE) {
            if (flag) textField.appendText(button.getText() + '(');
        }
        else if (button == buttonSqr) {
            if (flag) textField.appendText("^2");
        }
        else if (button == buttonN) {
            if (flag) textField.appendText("^");
        }
        else if (button == buttonSqrt) {
            if (flag) textField.appendText("√");
        }
        else if (button == buttonC) {
            textField.clear();
            flag = true;
        }
        else if (button == buttonX) {
            if (flag) {
                String text = textField.getText().substring(0, textField.getText().length() - 1);
                textField.clear();
                textField.appendText(text);
            }
        }
        else if (button == buttonResult) {
            if (flag) {
                String s = transform1(textField.getText());
                checkCorrectness(s);
                if (!textField.getText().equals("")) {
                    answer = calculate(transform2(s));
                }
                if (!textField.getText().equals("")) {
                    textField.setText(answer);
                    flag = false;
                }
            }
        }
        else {
            if (flag) textField.appendText(button.getText());
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        textField = new TextField();
        textField.setMinSize(stage.getWidth(), 20);
        textField.setEditable(false);

        gridPane = new GridPane();
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(30);
        gridPane.getRowConstraints().add(row1);
        gridPane.add(textField, 0, 0);
        GridPane.setHgrow(textField, Priority.ALWAYS);

        buttons = new GridPane();
        buttons.add(buttonSin = new Button("sin"), 0, 0);
        buttonSin.setMinSize(40, 40);
        buttonSin.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonSin));
        buttons.add(buttonOpeningBracket = new Button("("), 1, 0);
        buttonOpeningBracket.setMinSize(40, 40);
        buttonOpeningBracket.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonOpeningBracket));
        buttons.add(buttonClosingBracket = new Button(")"), 2, 0);
        buttonClosingBracket.setMinSize(40, 40);
        buttonClosingBracket.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonClosingBracket));
        buttons.add(buttonC = new Button("C"), 3, 0);
        buttonC.setMinSize(40, 40);
        buttonC.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonC));
        buttons.add(buttonX = new Button("X"), 4, 0);
        buttonX.setMinSize(40, 40);
        buttonX.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonX));

        buttons.add(buttonCos = new Button("cos"), 0, 1);
        buttonCos.setMinSize(40, 40);
        buttonCos.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonCos));
        buttons.add(buttonSqr = new Button("x^2"), 1, 1);
        buttonSqr.setMinSize(40, 40);
        buttonSqr.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonSqr));
        buttons.add(buttonSqrt = new Button("√x"), 2, 1);
        buttonSqrt.setMinSize(40, 40);
        buttonSqrt.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonSqrt));
        buttons.add(buttonN = new Button("x^n"), 3, 1);
        buttonN.setMinSize(40, 40);
        buttonN.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonN));
        buttons.add(buttonDivision = new Button("/"), 4, 1);
        buttonDivision.setMinSize(40, 40);
        buttonDivision.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonDivision));

        buttons.add(buttonTg = new Button("tg"), 0, 2);
        buttonTg.setMinSize(40, 40);
        buttonTg.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonTg));
        buttons.add(button7 = new Button("7"), 1, 2);
        button7.setMinSize(40, 40);
        button7.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(button7));
        buttons.add(button8 = new Button("8"), 2, 2);
        button8.setMinSize(40, 40);
        button8.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(button8));
        buttons.add(button9 = new Button("9"), 3, 2);
        button9.setMinSize(40, 40);
        button9.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(button9));
        buttons.add(buttonMultiplication = new Button("*"), 4, 2);
        buttonMultiplication.setMinSize(40, 40);
        buttonMultiplication.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonMultiplication));

        buttons.add(buttonCtg = new Button("ctg"), 0, 3);
        buttonCtg.setMinSize(40, 40);
        buttonCtg.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonCtg));
        buttons.add(button4 = new Button("4"), 1, 3);
        button4.setMinSize(40, 40);
        button4.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(button4));
        buttons.add(button5 = new Button("5"), 2, 3);
        button5.setMinSize(40, 40);
        button5.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(button5));
        buttons.add(button6 = new Button("6"), 3, 3);
        button6.setMinSize(40, 40);
        button6.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(button6));
        buttons.add(buttonSubtraction = new Button("-"), 4, 3);
        buttonSubtraction.setMinSize(40, 40);
        buttonSubtraction.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonSubtraction));

        buttons.add(buttonLn = new Button("ln"), 0, 4);
        buttonLn.setMinSize(40, 40);
        buttonLn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonLn));
        buttons.add(button1 = new Button("1"), 1, 4);
        button1.setMinSize(40, 40);
        button1.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(button1));
        buttons.add(button2 = new Button("2"), 2, 4);
        button2.setMinSize(40, 40);
        button2.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(button2));
        buttons.add(button3 = new Button("3"), 3, 4);
        button3.setMinSize(40, 40);
        button3.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(button3));
        buttons.add(buttonSum = new Button("+"), 4, 4);
        buttonSum.setMinSize(40, 40);
        buttonSum.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonSum));

        buttons.add(buttonLg = new Button("lg"), 0, 5);
        buttonLg.setMinSize(40, 40);
        buttonLg.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonLg));
        buttons.add(buttonE = new Button("exp"), 1, 5);
        buttonE.setMinSize(40, 40);
        buttonE.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonE));
        buttons.add(button0 = new Button("0"), 2, 5);
        button0.setMinSize(40, 40);
        button0.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(button0));
        buttons.add(buttonComma = new Button(","), 3, 5);
        buttonComma.setMinSize(40, 40);
        buttonComma.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonComma));
        buttons.add(buttonResult = new Button("="), 4, 5);
        buttonResult.setMinSize(40, 40);
        buttonResult.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> click(buttonResult));

        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(70);
        gridPane.getRowConstraints().add(row2);
        gridPane.add(buttons, 0, 1);
        GridPane.setHgrow(buttons, Priority.ALWAYS);
        GridPane.setVgrow(buttons, Priority.ALWAYS);

        Scene scene = new Scene(gridPane);
        stage.setFullScreen(false);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Калькулятор");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}