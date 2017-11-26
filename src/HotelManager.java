import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.util.Random;

public class HotelManager implements ActionListener {
    private static Connection database;
    private String db_id = "database";
    private String db_pw = "database";

    // GUI 원소들을 정의함.
    private JFrame frame = new JFrame();
    private JPanel rowPanel = new JPanel();
    private JPanel colPanel = new JPanel();
    private JPanel bookPanel = new JPanel();
    private JPanel nowPanel = new JPanel();
    private JPanel joinPanel = new JPanel();

    private JMenuBar menuBar = new JMenuBar();
    private JMenu fileMenu = new JMenu("파일");
    private JMenuItem fileMenuItem = new JMenuItem("열기");

    private JLabel mainText = new JLabel("호텔 관리체계");
    private JLabel nameLabel = new JLabel("고객명");
    private JLabel checkinLabel = new JLabel("체크인(YYYYMMDD)");
    private JLabel daysLabel = new JLabel("묵을 총 날짜");
    private JLabel roomLabel = new JLabel("객실");
    private JButton bookApply = new JButton("예약 등록 및 변경");
    private JTextField nameField = new JTextField();
    private JTextField checkinField = new JTextField();
    private String[] days = {"1", "2", "3", "4", "5"};
    private JComboBox daysBox = new JComboBox(days);
    private JComboBox roomBox = new JComboBox();
    private JButton bookCancel = new JButton("예약 취소");
    private JTabbedPane joinPane = new JTabbedPane();

    private JButton custJoin = new JButton("회원가입");
    private JButton staffJoin = new JButton("직원등록");

    private JFrame customerJoinFrame = new JFrame();
    private JPanel customerJoinPanel = new JPanel();

    private JLabel customerJoinNameLabel = new JLabel("고객명");
    private JLabel customerJoinSexLabel = new JLabel("성별");
    private JLabel customerJoinAddressLabel = new JLabel("주소");
    private JLabel customerJoinPhoneLabel = new JLabel("연락처");

    private String[] sexs = {"남", "여"};
    private String[] citys = {"인천", "경기", "서울", "충남", "제주", "전북", "경북", "울산", "강원", "부산", "대구"};
    private JTextField customerJoinNameInput = new JTextField();
    private JComboBox customerJoinSexBox = new JComboBox(sexs);
    private JComboBox customerJoinAddressBox = new JComboBox(citys);
    private JTextField customerJoinPhoneInput = new JTextField();

    private JButton customerJoinJoinButton = new JButton("가입신청");
    private JButton customerJoinCancelButton = new JButton("취소");

    //staff
    private JFrame staffJoinFrame = new JFrame();
    private JPanel staffJoinPanel = new JPanel();

    private JLabel staffJoinNameLabel = new JLabel("직원명");
    private JLabel staffJoinSexLabel = new JLabel("성별");
    private JLabel staffJoinAddressLabel = new JLabel("주소");
    private JLabel staffJoinPhoneLabel = new JLabel("연락처");

    private JTextField staffJoinNameInput = new JTextField();
    private JComboBox staffJoinSexBox = new JComboBox(sexs);
    private JComboBox staffJoinAddressBox = new JComboBox(citys);
    private JTextField staffJoinPhoneInput = new JTextField();

    private JButton staffJoinJoinButton = new JButton("가입신청");
    private JButton staffJoinCancelButton = new JButton("취소");

    // 검색용
    private JLabel custSearchLabel = new JLabel("고객명");
    private JTextField custSearchInput = new JTextField();
    private JButton custSearchButton = new JButton("조회");

    private JLabel staffSearchLabel = new JLabel("직원명");
    private JTextField staffSearchInput = new JTextField();
    private JButton staffSearchButton = new JButton("조회");

    private JTextArea custSearchOut = new JTextArea();
    private JTextArea staffSearchOut = new JTextArea();

    private Border blackline = BorderFactory.createLineBorder(Color.black);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    private Date date = new Date();
    private String now_date = dateFormat.format(date);
    private SimpleDateFormat tonowdateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    private String to_now_date = tonowdateFormat.format(date);


    public HotelManager() {
        //디비에 연결함. 및 등
        connectDB();
        rowPanel.setLayout(new GridLayout(3, 1));
        colPanel.setLayout(new GridLayout(1, 2));

        bookPanel.setLayout(new GridLayout(5, 2));
        bookPanel.setBorder(new TitledBorder("투숙 예약"));
        nowPanel.setLayout(new GridLayout(4, 5));


        nowPanel.setBorder(new TitledBorder("객실 예약 현황　　　　　　　　　　　　　　" + now_date));
        joinPanel.setLayout(new GridLayout(3, 1));
        joinPanel.setBorder(new TitledBorder("등록 및 조회"));

        //menu
        menuBar.add(fileMenu);
        fileMenu.add(fileMenuItem);
        frame.setJMenuBar(menuBar);

        //bookPanel
        bookPanel.add(nameLabel);
        bookPanel.add(nameField);
        bookPanel.add(checkinLabel);
        bookPanel.add(checkinField);
        bookPanel.add(daysLabel);
        bookPanel.add(daysBox);
        bookPanel.add(roomLabel);
        bookPanel.add(roomBox);
        bookPanel.add(bookApply);
        bookPanel.add(bookCancel);
        bookApply.addActionListener(this);

        //nowPanel
        try {
            printReserved();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //콤보박스에 디비 받아서 룸들 더함.
        try {
            addRoomsToBOX();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //joinPanel
        //joinPanel.add(joinPane);
        //test 용으로 각종 엘레멘트 등록해봄.
        JPanel testcust = new JPanel(new GridLayout(2, 6));
        joinPanel.add(testcust);
        testcust.add(custSearchLabel);
        testcust.add(custSearchInput);
        testcust.add(custSearchButton);
        custSearchButton.addActionListener(this);
        testcust.add(custJoin);
        custJoin.addActionListener(this);

        JPanel teststaff = new JPanel(new GridLayout(2, 6));
        teststaff.add(staffSearchLabel);
        teststaff.add(staffSearchInput);
        teststaff.add(staffSearchButton);
        staffSearchButton.addActionListener(this);
        teststaff.add(staffJoin);
        staffJoin.addActionListener(this);
        joinPanel.add(teststaff);

        JPanel testOut = new JPanel(new GridLayout(1, 2));
        custSearchOut.setEditable(false);
        testOut.add(custSearchOut);
        testOut.add(staffSearchOut);
        joinPanel.add(testOut);

        JPanel mainTextPanel = new JPanel(new GridLayout(2, 1));
        mainText.setFont(new Font("맑은 고딕", 1, 24));
        mainText.setHorizontalAlignment(SwingConstants.CENTER);
        JTextArea help = new JTextArea();
        help.setEditable(false);
        help.setText("설명:\n과제 스펙 이외에 예외사항은 별도 예외처리안함.");
        mainTextPanel.add(mainText);
        mainTextPanel.add(help);
        rowPanel.add(mainTextPanel);
        rowPanel.add(colPanel);
        rowPanel.add(joinPanel);
        colPanel.add(bookPanel);
        colPanel.add(nowPanel);

        frame.add(rowPanel);

        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        customerJoinPanel.setLayout(new GridLayout(5, 2));
        customerJoinPanel.add(customerJoinNameLabel);
        customerJoinPanel.add(customerJoinNameInput);
        customerJoinPanel.add(customerJoinSexLabel);
        customerJoinPanel.add(customerJoinSexBox);
        customerJoinPanel.add(customerJoinAddressLabel);
        customerJoinPanel.add(customerJoinAddressBox);
        customerJoinPanel.add(customerJoinPhoneLabel);
        customerJoinPanel.add(customerJoinPhoneInput);
        customerJoinPanel.add(customerJoinJoinButton);
        customerJoinPanel.add(customerJoinCancelButton);
        customerJoinCancelButton.addActionListener(this);
        customerJoinJoinButton.addActionListener(this);

        customerJoinFrame.add(customerJoinPanel);
        customerJoinFrame.setSize(300,400);
        customerJoinFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //staff
        staffJoinPanel.setLayout(new GridLayout(5, 2));
        staffJoinPanel.add(staffJoinNameLabel);
        staffJoinPanel.add(staffJoinNameInput);
        staffJoinPanel.add(staffJoinSexLabel);
        staffJoinPanel.add(staffJoinSexBox);
        staffJoinPanel.add(staffJoinAddressLabel);
        staffJoinPanel.add(staffJoinAddressBox);
        staffJoinPanel.add(staffJoinPhoneLabel);
        staffJoinPanel.add(staffJoinPhoneInput);
        staffJoinPanel.add(staffJoinJoinButton);
        staffJoinPanel.add(staffJoinCancelButton);
        staffJoinJoinButton.addActionListener(this);
        staffJoinCancelButton.addActionListener(this);

        staffJoinFrame.add(staffJoinPanel);
        staffJoinFrame.setSize(300,400);
        staffJoinFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void customerJoin() {
        customerJoinFrame.setVisible(true);
    }

    public void staffJoin() {
        staffJoinFrame.setVisible(true);
    }
    public void custJoinInsert() throws SQLException{
        //custJoin판넬에서 받은 결과를 DB에 저장한다.
        String name = customerJoinNameInput.getText();
        String sex = (String)customerJoinSexBox.getSelectedItem();
        String address = (String)customerJoinAddressBox.getSelectedItem();
        String phone = customerJoinPhoneInput.getText();

        String sqlStr = "INSERT INTO customers VALUES ('" + name + "','" +
                sex + "','" + address + "'," + phone + ")";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        stmt.executeQuery();
        stmt.close();

        //성공적으로 저장이 되면
        System.out.print("성공적으로 저장됨.");
        customerJoinFrame.setVisible(false);
    }

    public void staffJoinInsert() throws SQLException{
        //staffjoin 판넬에서 받은 결과를 DB에 저장한다.
        String name = staffJoinNameInput.getText();
        String sex = (String)staffJoinSexBox.getSelectedItem();
        String address = (String)staffJoinAddressBox.getSelectedItem();
        String phone = staffJoinPhoneInput.getText();

        String sqlStr = "INSERT INTO staff VALUES ('" + name + "','" +
                sex + "','" + address + "'," + phone + ")";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        stmt.executeQuery();
        stmt.close();

        //성공적으로 저장이 되면
        System.out.print("성공적으로 저장됨.");
        staffJoinFrame.setVisible(false);
    }

    public void bookInsert() throws SQLException{
        //예약 결과를 등록한다.
        String cust_name = nameField.getText();
        String checkIn = checkinField.getText();
        String days = (String)daysBox.getSelectedItem();
        String room_number = (String)roomBox.getSelectedItem();
        String staff_name;

        String sqlStr = "SELECT name FROM staff";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        staff_name = rs.getString("name");
        Random random = new Random();

        while(rs.next() && random.nextBoolean()) {
            staff_name = rs.getString("name");
        }
        stmt.close();
        for (int i = 0; i < Integer.parseInt(days); i++) {
            sqlStr = "INSERT INTO reservation VALUES (" + room_number + ",'" +
                    cust_name + "','" + staff_name + "',to_date('" +
                    String.valueOf(Integer.parseInt(checkIn) + i) +"','YYYYMMDD'),to_date('"  +
                    checkIn + "','YYYYMMDD'))";
            stmt = database.prepareStatement(sqlStr);
            stmt.executeQuery();
        }

        stmt.close();

        printReserved();
    }

    public void printReserved() throws SQLException{
        //예약현황을 프린트한다.
        JLabel[] rooms;
        rooms = new JLabel[20];
        nowPanel.removeAll();
        nowPanel.revalidate();

        String insqlStr;
        PreparedStatement instmt;
        ResultSet inrs;

        String sqlStr = "SELECT room_number FROM rooms";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        ResultSet rs = stmt.executeQuery();
        int i = 0;
        while(rs.next() && i < 20) {
            rooms[i] = new JLabel(rs.getString("room_number"), JLabel.CENTER);

            //만약 예약되었다면
            insqlStr = "SELECT * FROM reservation where day = to_date('"+
                    to_now_date+"','YYYYMMDD') and room_number = "+rs.getString("room_number");
            instmt = database.prepareStatement(insqlStr);
            inrs = instmt.executeQuery();
            if (inrs.next()) {
                rooms[i].setForeground(Color.red);
            }

            rooms[i].setBorder(blackline);
            nowPanel.add(rooms[i]);
            i++;
        }
        stmt.close();
    }

    public void addRoomsToBOX() throws SQLException{
        String sqlStr = "SELECT room_number FROM rooms";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            roomBox.addItem(rs.getString("room_number"));
        }

        stmt.close();
    }

    public void searchCust() throws SQLException{
        // 고객을 조회한다.
        // 고객 정보 조회
        String name = custSearchInput.getText();
        String sqlStr = "SELECT * FROM customers WHERE name ='"+name+"'";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        String sex = rs.getString("sex");
        String address = rs.getString("address");
        String phone = rs.getString("phone");
        rs.next();
        stmt.close();
        //투숙기간 카운팅
        sqlStr = "SELECT count(*) count FROM reservation WHERE customer_name ='"+name+"'";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
        rs.next();
        String countDay = rs.getString("count");
        rs.next();
        stmt.close();
        //최근 투숙일 받기, 분명 이런식으로 하는 것보다 더 좋은 방법이 있겠지만 시간과 지식이 부족함.
        sqlStr = "SELECT * FROM reservation WHERE customer_name ='"+name+"' order by day desc";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
        rs.next();
        String lastDay = rs.getString("day");
        rs.next();
        stmt.close();
        //최다직원 받기
        sqlStr = "SELECT staff_name, count(staff_name) FROM (select DISTINCT room_number, customer_name, staff_name, check_in from reservation) WHERE customer_name ='"+name+"' group by staff_name order by count(staff_name) desc";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
        rs.next();
        String staffCount = rs.getString("count(staff_name)");
        String staffname = rs.getString("staff_name");
        rs.next();
        stmt.close();
        custSearchOut.setText("고객명: "+name+"\n성별: "+sex+
                "\n주소: "+address+"\n연락처: "+phone +"\n총 투숙기간: "+
                countDay+"\n최근 투숙일: "+lastDay+"\n객실전담직원(최다): "+staffname+"("+staffCount+"회)");
    }

    public void searchRoom() throws SQLException{
        //방번호로 방을 조회한다.
    }

    public void searchSfaff() throws SQLException{
        // 이름으로 직원을 조회한다.
        // 직원 정보 조회
        String name = staffSearchInput.getText();
        String sqlStr = "SELECT * FROM staff WHERE name ='"+name+"'";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        String sex = rs.getString("sex");
        String address = rs.getString("address");
        String phone = rs.getString("phone");
        rs.next();
        stmt.close();
        //접대 고객 최다
        sqlStr = "SELECT customer_name, count(customer_name) FROM (select DISTINCT room_number, customer_name, staff_name, check_in from reservation) WHERE staff_name ='"+name+"' group by customer_name order by count(customer_name) desc";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
        rs.next();
        String cust_name = rs.getString("customer_name");
        String cust_count = rs.getString("count(customer_name)");
        rs.next();
        stmt.close();
        //최다 객실 받기
        sqlStr = "SELECT room_number, count(room_number) FROM (select DISTINCT room_number, customer_name, staff_name, check_in from reservation) WHERE staff_name ='"+name+"' group by room_number order by count(room_number) desc";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
        rs.next();
        String room_number = rs.getString("room_number");
        String room_count = rs.getString("count(room_number)");
        rs.next();
        stmt.close();
        staffSearchOut.setText("직원명: "+name+"\n성별: "+sex+
                "\n주소: "+address+"\n연락처: "+phone +"\n접대고객(최다): "+
                cust_name+"("+cust_count+"회)"+"\n관리객실(최다): "+room_number+"("+room_count+"회)");
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == custJoin) {
            customerJoin();
        } else if(e.getSource() == staffJoin) {
            staffJoin();
        } else if(e.getSource() == customerJoinCancelButton) {
            customerJoinFrame.setVisible(false);
        } else if(e.getSource() == customerJoinJoinButton) {
            try{
                custJoinInsert();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } else if(e.getSource() == staffJoinCancelButton) {
            staffJoinFrame.setVisible(false);
        } else if(e.getSource() == staffJoinJoinButton) {
            try{
                staffJoinInsert();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } else if(e.getSource() == custSearchButton) {
            try {
                searchCust();
            } catch (SQLException se) {
                custSearchOut.setText("결과 없습니다.");
            }
        } else if(e.getSource() == staffSearchButton) {
            try {
                searchSfaff();
            } catch (SQLException se) {
                staffSearchOut.setText("결과 없습니다.");
            }
        } else if(e.getSource() == bookApply) {
            try {
                bookInsert();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void connectDB() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            database = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", db_id, db_pw);
            System.out.println("DB, OK!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQLExeption:"+e);
        } catch (Exception e) {
            System.out.println("Exception: "+e);
        }
    }

    public static void main(String[] argv) {
        new HotelManager();
    }
}
