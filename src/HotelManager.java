import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private JTextArea roomSearchOut = new JTextArea();

    private Border blackline = BorderFactory.createLineBorder(Color.black);

    private JComboBox roomPageBox = new JComboBox();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    private Date date = new Date();
    private String now_date = dateFormat.format(date);
    private SimpleDateFormat tonowdateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
    private String to_now_date = tonowdateFormat.format(date);

    private JPanel mainPanel = new JPanel(null);


    public HotelManager() {
        //디비에 연결함. 및 등
        connectDB();
        //로그인창 구성하시오

        //menu구성
        menuBar.add(fileMenu);
        fileMenu.add(fileMenuItem);
        frame.setJMenuBar(menuBar);

        //최상단 제목 및 설명 구성
        mainText.setFont(new Font("맑은 고딕", 1, 24));
        mainText.setHorizontalAlignment(SwingConstants.CENTER);
        mainText.setBorder(blackline);
        JTextArea help = new JTextArea();
        help.setEditable(false);
        help.setText("###########꼭 읽어주세요###########\n" +
                "1: 과제 명시 없는 부분 콘솔 SQL 에러 호출\n" +
                "2: 텍스트 파일 유니코드 사용 바람(한글깨짐)\n" +
                "3: 예약 변경 버튼 -> 자동으로 사이의 체크인이 같은 날자들 취소함\n" +
                "4: 예약 취소 버튼 -> 체크인 날짜만 받아서 취소함");
        mainText.setBounds(100, 30, 200, 50);
        mainPanel.add(mainText);
        help.setBounds(400, 0, 400, 100);
        mainPanel.add(help);

        //예약 패널
        bookPanel.setLayout(null);
        bookPanel.setBorder(new TitledBorder("투숙 예약"));
        bookPanel.setBounds(20, 120, 350, 300);
        mainPanel.add(bookPanel);

        nameLabel.setBounds(40, 50, 50, 20);
        bookPanel.add(nameLabel);
        nameField.setBounds(170, 50, 120, 20);
        bookPanel.add(nameField);
        checkinLabel.setBounds(40,80,120,20);
        bookPanel.add(checkinLabel);
        checkinField.setBounds(170,80,120,20);
        bookPanel.add(checkinField);
        daysLabel.setBounds(40, 110, 120, 20);
        bookPanel.add(daysLabel);
        daysBox.setBounds(170, 110, 120, 20);
        bookPanel.add(daysBox);
        roomLabel.setBounds(40,140,50,20);
        bookPanel.add(roomLabel);
        roomBox.setBounds(170,140,120,20);
        bookPanel.add(roomBox);
        bookApply.setBounds(40,170,250,40);
        bookPanel.add(bookApply);
        bookCancel.setBounds(40, 220, 250, 40);
        bookCancel.addActionListener(this);
        bookPanel.add(bookCancel);
        bookApply.addActionListener(this);
        try { // DB에서 방들 불러와 룸박스에 저장
            addRoomsToBOX(roomBox);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //현재 패널
        nowPanel.setLayout(null);
        nowPanel.setBorder(new TitledBorder("객실 예약 현황(" + now_date + ")"));
        nowPanel.setBounds(400, 120, 350, 300);
        mainPanel.add(nowPanel);
        try { // 패널에 방 현황을 프린트함
            printReserved();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //가입 조회 패널
        joinPanel.setLayout(null);
        joinPanel.setBorder(new TitledBorder("등록 및 조회"));
        joinPanel.setBounds(20, 450, 730, 250);
        mainPanel.add(joinPanel);


        //고객 빠네
        JPanel custPage = new JPanel(null);
        joinPane.setBounds(20, 20, 690, 220);
        joinPanel.add(joinPane);
        custSearchLabel.setBounds(20, 50, 70, 30);
        custPage.add(custSearchLabel);
        custSearchInput.setBounds(90, 50, 120, 30);
        custPage.add(custSearchInput);
        custSearchButton.setBounds(20, 100, 90, 50);
        custPage.add(custSearchButton);
        custSearchButton.addActionListener(this);
        custJoin.setBounds(120, 100, 90, 50);
        custPage.add(custJoin);
        custJoin.addActionListener(this);
        custSearchOut.setBounds(300, 20, 350, 150);
        custPage.add(custSearchOut);
        custSearchOut.setEditable(false);

        //객실 빠네
        try {
            addRoomsToBOX(roomPageBox);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JPanel roomPage = new JPanel(null);
        JLabel roomPageLabel = new JLabel("객실");
        roomPageLabel.setBounds(20, 50, 70, 30);
        roomPage.add(roomPageLabel);
        roomPage.add(roomPageBox);
        roomPageBox.setBounds(90, 50, 120, 30);
        roomPageBox.addActionListener(this);
        roomSearchOut.setBounds(300, 20, 350, 150);
        roomSearchOut.setEditable(false);
        roomPage.add(roomSearchOut);

        //직원 빠네
        JPanel staffPage = new JPanel(null);
        staffSearchLabel.setBounds(20, 50, 70, 30);
        staffPage.add(staffSearchLabel);
        staffSearchInput.setBounds(90, 50, 120, 30);
        staffPage.add(staffSearchInput);
        staffSearchButton.setBounds(20, 100, 90, 50);
        staffPage.add(staffSearchButton);
        staffSearchButton.addActionListener(this);
        staffJoin.setBounds(120, 100, 90, 50);
        staffPage.add(staffJoin);
        staffJoin.addActionListener(this);
        staffPage.add(staffSearchOut);
        staffSearchOut.setBounds(300, 20, 350, 150);
        staffSearchOut.setEditable(false);

        joinPane.add("고객",custPage);
        joinPane.add("객실", roomPage);
        joinPane.add("직원",staffPage);

        frame.add(mainPanel);

        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        customerJoinPanel.setLayout(null);
        customerJoinNameLabel.setBounds(40, 30, 70, 30);
        customerJoinPanel.add(customerJoinNameLabel);
        customerJoinNameInput.setBounds(120,30,120,30);
        customerJoinPanel.add(customerJoinNameInput);
        customerJoinSexLabel.setBounds(40, 70, 70, 30);
        customerJoinPanel.add(customerJoinSexLabel);
        customerJoinSexBox.setBounds(120,70,120,30);
        customerJoinPanel.add(customerJoinSexBox);
        customerJoinAddressLabel.setBounds(40, 110, 70, 30);
        customerJoinPanel.add(customerJoinAddressLabel);
        customerJoinAddressBox.setBounds(120,110,120,30);
        customerJoinPanel.add(customerJoinAddressBox);
        customerJoinPhoneLabel.setBounds(40, 150, 70, 30);
        customerJoinPanel.add(customerJoinPhoneLabel);
        customerJoinPhoneInput.setBounds(120,150,120,30);
        customerJoinPanel.add(customerJoinPhoneInput);
        customerJoinJoinButton.setBounds(40, 190, 90, 50);
        customerJoinPanel.add(customerJoinJoinButton);
        customerJoinCancelButton.setBounds(150, 190, 90, 50);
        customerJoinPanel.add(customerJoinCancelButton);
        customerJoinCancelButton.addActionListener(this);
        customerJoinJoinButton.addActionListener(this);

        customerJoinFrame.add(customerJoinPanel);
        customerJoinFrame.setSize(300,320);
        customerJoinFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //staff
        staffJoinPanel.setLayout(null);
        staffJoinNameLabel.setBounds(40, 30, 70, 30);
        staffJoinPanel.add(staffJoinNameLabel);
        staffJoinNameInput.setBounds(120,30,120,30);
        staffJoinPanel.add(staffJoinNameInput);
        staffJoinSexLabel.setBounds(40, 70, 70, 30);
        staffJoinPanel.add(staffJoinSexLabel);
        staffJoinSexBox.setBounds(120,70,120,30);
        staffJoinPanel.add(staffJoinSexBox);
        staffJoinAddressLabel.setBounds(40, 110, 70, 30);
        staffJoinPanel.add(staffJoinAddressLabel);
        staffJoinAddressBox.setBounds(120,110,120,30);
        staffJoinPanel.add(staffJoinAddressBox);
        staffJoinPhoneLabel.setBounds(40, 150, 70, 30);
        staffJoinPanel.add(staffJoinPhoneLabel);
        staffJoinPhoneInput.setBounds(120,150,120,30);
        staffJoinPanel.add(staffJoinPhoneInput);
        staffJoinJoinButton.setBounds(40, 190, 90, 50);
        staffJoinPanel.add(staffJoinJoinButton);
        staffJoinCancelButton.setBounds(150, 190, 90, 50);
        staffJoinPanel.add(staffJoinCancelButton);
        staffJoinJoinButton.addActionListener(this);
        staffJoinCancelButton.addActionListener(this);
        fileMenuItem.addActionListener(new FileOpen());

        staffJoinFrame.add(staffJoinPanel);
        staffJoinFrame.setSize(300,320);
        staffJoinFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    class FileOpen implements ActionListener {
        JFileChooser chooser;

        FileOpen() {
            chooser = new JFileChooser();
        }

        public void actionPerformed(ActionEvent e) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("txt","txt");
            chooser.setFileFilter(filter);

            int ret = chooser.showOpenDialog(null);
            if(ret != JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(null, "취소함","경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String filePath = chooser.getSelectedFile().getPath();

            try{
                insertTXTinDB(filePath);
            } catch (SQLException se) {
                se.printStackTrace();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
    }

    public void insertTXTinDB(String path) throws SQLException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();
        //고객등록
        int number = Integer.parseInt(line.substring(1, line.length())); // 뭔가 텍스트로 저장하면 앞에 문자열이 있나봄
        for (int i = 0; i < number; i++) {
            line = br.readLine();
            String[] words = line.split("\t");
            String sqlStr = "INSERT INTO customers VALUES(?, ?, ?, ?)";
            PreparedStatement stmt = database.prepareStatement(sqlStr);
            stmt.setString(1, words[0]);
            stmt.setString(2, words[1]);
            stmt.setString(3, words[2]);
            stmt.setString(4, words[3]);
            stmt.executeQuery();
            stmt.close();
        }
        line = br.readLine();
        number = Integer.parseInt(line);
        for (int i = 0; i < number; i++) {
            line = br.readLine();
            String[] words = line.split("\t");
            String sqlStr = "INSERT INTO staff VALUES(?, ?, ?, ?)";
            PreparedStatement stmt = database.prepareStatement(sqlStr);
            stmt.setString(1, words[0]);
            stmt.setString(2, words[1]);
            stmt.setString(3, words[2]);
            stmt.setString(4, words[3]);
            stmt.executeQuery();
            stmt.close();
        }
        line = br.readLine();
        number = Integer.parseInt(line);
        for (int i = 0; i < number; i++) {
            line = br.readLine();
            String[] words = line.split("\t");
            String sqlStr = "INSERT INTO rooms VALUES(?, ?, ?)";
            PreparedStatement stmt = database.prepareStatement(sqlStr);
            stmt.setString(1, words[0]);
            stmt.setString(2, words[1]);
            stmt.setString(3, words[2]);
            stmt.executeQuery();
            stmt.close();
        }
        br.close();
        printReserved();
        try {
            addRoomsToBOX(roomBox);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            addRoomsToBOX(roomPageBox);
        } catch (SQLException e) {
            e.printStackTrace();
        }

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

    public void bookCancel() throws SQLException{
        String cust_name = nameField.getText();
        String checkIn = checkinField.getText();
        String sqlStr = "DELETE FROM reservation WHERE customer_name ='" + cust_name + "' and check_in = to_date('" +checkIn + "','YYYYMMDD')";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        stmt.executeQuery();
        System.out.println("기등록 예약 취소 완료입니다.");
        nowPanel.removeAll();
        nowPanel.revalidate();

        printReserved();

        nowPanel.repaint();
    }
    public void bookInsert() throws SQLException{
        //예약 결과를 등록한다.
        String cust_name = nameField.getText();
        String checkIn = checkinField.getText();
        String days = (String)daysBox.getSelectedItem();
        String room_number = (String)roomBox.getSelectedItem();
        String staff_name;
        ResultSet rs;

        //이미 등록된 방일 때
        String sqlStr = "SELECT * FROM reservation WHERE room_number ="+room_number+" and day BETWEEN to_date('"+checkIn +"','YYYYMMDD') and to_date('"+ checkIn + "','YYYYMMDD') +"+String.valueOf(Integer.parseInt(days) - 1);
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();

        if(rs.next()) {
            System.out.println("이미 찬 방이기 때문에 예약이 중지됩니다.");
            return;
        }

        //이미 예약 처리 (고객이)
        sqlStr = "SELECT DISTINCT check_in FROM reservation WHERE customer_name ='" + cust_name + "' and day BETWEEN to_date('" + checkIn + "','YYYYMMDD') and to_date('" + checkIn + "','YYYYMMDD') + "+days+" - 1";
         stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
        while(rs.next()) {
            String check_in = rs.getString("check_in");
            String[] check_ins = check_in.split(" ");
            System.out.print(check_ins[0]);
            sqlStr = "DELETE FROM reservation WHERE customer_name ='" + cust_name + "' and check_in = '"+check_ins[0]+"'";
            stmt = database.prepareStatement(sqlStr);
            stmt.executeQuery();
            System.out.println("기등록된 예약이 있어서 취소했습니다.");
        };

        sqlStr = "SELECT name FROM staff";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
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
                    checkIn +"','YYYYMMDD')+"+String.valueOf(i)+",to_date('"  +
                    checkIn + "','YYYYMMDD')) ";
            stmt = database.prepareStatement(sqlStr);
            stmt.executeQuery();
        }
        stmt.close();

        nowPanel.removeAll();
        nowPanel.revalidate();

        printReserved();

        nowPanel.repaint();

        System.out.println("성공적으로 예약했습니다.");
    }

    public void printReserved() throws SQLException{
        //예약현황을 프린트한다.
        JLabel[] rooms;
        rooms = new JLabel[20];

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
                rooms[i].setBackground(Color.yellow);
                rooms[i].setOpaque(true);
            }

            rooms[i].setBorder(blackline);
            int yLocate = i / 5;
            int xLocate = i % 5;
            rooms[i].setBounds(10+ xLocate*65, 40 + yLocate*60, 60, 50);
            nowPanel.add(rooms[i]);
            i++;
        }
        stmt.close();
    }

    public void addRoomsToBOX(JComboBox box) throws SQLException{
        String sqlStr = "SELECT room_number FROM rooms";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            box.addItem(rs.getString("room_number"));
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
        String countDay;
        if (rs.next()) {
            countDay = rs.getString("count");
        } else {
            countDay = "정보가 없습니다.";
        }
        stmt.close();
        //최근 투숙일 받기, 분명 이런식으로 하는 것보다 더 좋은 방법이 있겠지만 시간과 지식이 부족함.
        sqlStr = "SELECT * FROM reservation WHERE customer_name ='"+name+"' order by day desc";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
        String lastDay;
        if (rs.next()) {
            lastDay = rs.getString("day");
        } else {
            lastDay = "정보가 없습니다.";
        }
        stmt.close();
        //최다직원 받기
        sqlStr = "SELECT staff_name, count(staff_name) FROM (select DISTINCT room_number, customer_name, staff_name, check_in from reservation) WHERE customer_name ='"+name+"' group by staff_name order by count(staff_name) desc";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
        String staffCount;
        String staffname;
        if (rs.next()) {
            staffCount = rs.getString("count(staff_name)");
            staffname = rs.getString("staff_name");
        } else {
            staffCount = "[정보 없음]";
            staffname = "정보가 없습니다.";
        }
        stmt.close();
        custSearchOut.setText("고객명: "+name+"\n성별: "+sex+
                "\n주소: "+address+"\n연락처: "+phone +"\n총 투숙기간: "+
                countDay+"\n최근 투숙일: "+lastDay+"\n객실전담직원(최다): "+staffname+"("+staffCount+"회)");
    }

    public void searchRoom() throws SQLException{
        //방번호로 방을 조회한다.
        String item = roomPageBox.getSelectedItem().toString();

        String sqlStr = "SELECT * FROM rooms WHERE room_number = ?";
        PreparedStatement stmt = database.prepareStatement(sqlStr);
        stmt.setString(1, item);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        String capacity = rs.getString("capacity");
        String type = rs.getString("type");
        rs.next();

        sqlStr = "SELECT * FROM reservation WHERE room_number = ? and day = to_date('"+to_now_date+"','YYYYMMDD')";
        stmt = database.prepareStatement(sqlStr);
        stmt.setString(1, item);
        rs = stmt.executeQuery();
        String now_avail;
        if (rs.next()) {
            now_avail = "투숙중";
        } else {
            now_avail = "비어있음";
        }
        //최다 투숙고객 찾기
        sqlStr = "SELECT customer_name, count(customer_name) FROM (" +
                "select DISTINCT room_number, customer_name, check_in from reservation)" +
                "where room_number = ? group by customer_name order by count(customer_name) desc";
        stmt = database.prepareStatement(sqlStr);
        stmt.setString(1, item);
        rs = stmt.executeQuery();
        String cust_info;
        if (rs.next()) {
            cust_info = rs.getString("customer_name");
            cust_info = cust_info + "(" + rs.getString("count(customer_name)")+"회)";
        } else {
            cust_info = "숙박한 사람이 없습니다.";
        }
        //최다 담당직원
        sqlStr = "SELECT staff_name, count(staff_name) FROM (" +
                "select DISTINCT room_number, staff_name, check_in from reservation)" +
                "where room_number = ? group by staff_name order by count(staff_name) desc";
        stmt = database.prepareStatement(sqlStr);
        stmt.setString(1, item);
        rs = stmt.executeQuery();
        String staff_info;
        if (rs.next()) {
            staff_info = rs.getString("staff_name");
            staff_info = staff_info + "(" + rs.getString("count(staff_name)")+"회)";
        } else {
            staff_info = "담당한 사람이 없습니다.";
        }
        roomSearchOut.setText("방번호: "+item+"\n수용인원: "+capacity+"\n타입: "+type+"\n상태: "+now_avail+
        "\n투숙고객(최다): " + cust_info+"\n객신전담직원(최다): "+staff_info);
        stmt.close();
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
        String cust_name;
        String cust_count;
        if (rs.next()) {
            cust_name = rs.getString("customer_name");
            cust_count = rs.getString("count(customer_name)");
        } else {
            cust_name = "정보가 없습니다";
            cust_count = "[정보없음]";
        }
        stmt.close();
        //최다 객실 받기
        sqlStr = "SELECT room_number, count(room_number) FROM (select DISTINCT room_number, customer_name, staff_name, check_in from reservation) WHERE staff_name ='"+name+"' group by room_number order by count(room_number) desc";
        stmt = database.prepareStatement(sqlStr);
        rs = stmt.executeQuery();
        String room_number;
        String room_count;
        if (rs.next()) {
            room_number = rs.getString("room_number");
            room_count = rs.getString("count(room_number)");
        } else {
            room_number = "정보가 없습니다.";
            room_count = "[정보없음]";
        }

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
        } else if(e.getSource() == roomPageBox) {
            try {
                searchRoom();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } else if(e.getSource() == bookCancel) {
            try {
                bookCancel();
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
