package com.aston.repository.test;

import com.aston.dz2.entity.*;
import com.aston.dz2.entity.enums.Level;
import com.aston.dz2.entity.enums.Semester;
import com.aston.dz2.entity.enums.TeacherRank;
import com.aston.dz2.repository.ConnectionBean;
import com.aston.dz2.repository.impl.*;
import com.aston.dz2.service.*;
import com.aston.dz2.service.impl.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DisciplineRepositoryIntegrationTest {

    private final ConnectionBean connectionBean = new ConnectionBean("jdbc:postgresql://localhost:8888/testdb");
    private final DepartmentRepository departmentRepository = new DepartmentRepository(connectionBean);
    private final DepartmentService departmentService = new DepartmentServiceImpl(departmentRepository);
    private final GroupRepository groupRepository = new GroupRepository(connectionBean, departmentService);
    private final TeacherRepository teacherRepository = new TeacherRepository(connectionBean, departmentService);

    private final TeacherService teacherService = new TeacherServiceImpl(teacherRepository, departmentService);
    private final GroupService groupService;
    private final StudentRepository studentRepository;
    private final StudentService studentService;
    private final DisciplineRepository disciplineRepository;
    private final DisciplineService disciplineService;

    private final Logger logger = LoggerFactory.getLogger(DisciplineRepositoryIntegrationTest.class);

    private Department department;
    private Group group;
    private Student student1;
    private Student student2;
    private Teacher lecturer;
    private Teacher assistant;

    public DisciplineRepositoryIntegrationTest() throws Exception {
        groupService = new GroupServiceImpl(groupRepository, departmentService, null, null);
        studentRepository = new StudentRepository(connectionBean, departmentService, groupService, LoggerFactory.getLogger(StudentRepository.class));
        studentService= new StudentServiceImpl(studentRepository, departmentService);
        disciplineRepository = new DisciplineRepository(groupService, teacherService, studentService, connectionBean);
        disciplineService = new DisciplineServiceImpl(disciplineRepository, teacherService, groupService);
        groupService.setDisciplineService(disciplineService);
        groupService.setStudentService(studentService);
        initializeDepartment();
        initializeGroup();
        initializeStudents();
        initializeTeacher();
    }

    @BeforeEach
    public void setUp(){
        connectionBean.initialize();
    }

    @AfterEach
    public void cleanUp(){
        connectionBean.closeConnection();
    }

    @Test
    public void createDiscipline() throws Exception {
        Discipline discipline = Discipline.builder()
                .name("Programming I")
                .lecturer(lecturer)
                .assistant(assistant)
                .semester(Semester.FIRST)
                .build();
        disciplineRepository.save(discipline);
        Discipline result = disciplineRepository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertAll(() -> {
            assertNotNull(result);
            assertEquals(discipline.getName(), result.getName());
            assertEquals(discipline.getLecturer(), result.getLecturer());
            assertEquals(discipline.getSemester(), result.getSemester());
        });
        disciplineRepository.deleteById(result.getId());
    }

    @Test
    public void addGroupToDiscipline() throws Exception{
        Discipline discipline = Discipline.builder()
                .name("Programming I")
                .lecturer(lecturer)
                .assistant(assistant)
                .semester(Semester.FIRST)
                .build();
        disciplineRepository.save(discipline);
        Discipline temp1 = disciplineRepository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertAll(() -> {
            assertNotNull(temp1);
            assertEquals(discipline.getName(), temp1.getName());
            assertEquals(discipline.getLecturer(), temp1.getLecturer());
            assertEquals(discipline.getSemester(), temp1.getSemester());
        });
        disciplineService.addGroupToDiscipline(temp1.getId(), group.getId());
        Discipline temp2 = disciplineRepository.getById(temp1.getId());
        assertAll(() -> {
            assertNotNull(temp2);
            assertTrue(temp2.getGroups().contains(group));
        });
        disciplineRepository.deleteById(temp2.getId());
    }

    @Test
    public void updateDiscipline() throws Exception{
        Discipline discipline = Discipline.builder()
                .name("Programming I")
                .lecturer(lecturer)
                .assistant(assistant)
                .semester(Semester.FIRST)
                .build();
        disciplineRepository.save(discipline);
        discipline = disciplineRepository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertNotNull(discipline);
        discipline.setName("Computer Architecture");
        discipline.setSemester(Semester.FIFTH);
        discipline.setLecturer(assistant);
        discipline.setAssistant(lecturer);
        disciplineRepository.save(discipline);
        Discipline result = disciplineRepository.getById(discipline.getId());
        Discipline expected = discipline;
        assertAll(() -> {
            assertNotNull(result);
            assertEquals(expected.getName(), result.getName());
            assertEquals(expected.getSemester(), result.getSemester());
            assertEquals(expected.getLecturer(), result.getLecturer());
            assertEquals(expected.getAssistant(), result.getAssistant());
        });
        disciplineRepository.deleteById(discipline.getId());
    }

    @Test
    public void deleteDiscipline() throws Exception{
        Discipline discipline = Discipline.builder()
                .name("Programming I")
                .lecturer(lecturer)
                .assistant(assistant)
                .semester(Semester.FIRST)
                .build();
        disciplineRepository.save(discipline);
        discipline = disciplineRepository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertNotNull(discipline);
        disciplineRepository.deleteById(discipline.getId());
        assertNull(disciplineRepository.getById(discipline.getId()));
    }

    @Test
    public void addScoresToStudents() throws Exception{
        Discipline discipline = Discipline.builder()
                .name("Programming I")
                .lecturer(lecturer)
                .assistant(assistant)
                .semester(Semester.FIRST)
                .build();
        disciplineRepository.save(discipline);
        discipline = disciplineRepository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertNotNull(discipline);
        disciplineService.addGroupToDiscipline(discipline.getId(), group.getId());
        List<Student> students = groupRepository.getById(group.getId()).getStudents().stream().toList();
        disciplineRepository.saveStudentScoreToDiscipline(discipline.getId(), students.get(0).getId(), 50);
        disciplineRepository.saveStudentScoreToDiscipline(discipline.getId(), students.get(1).getId(), 100);
        StudentDisciplineScore firstStudentScore = disciplineRepository.getDisciplineScoreByStudentId(discipline.getId(), students.get(0).getId());
        Discipline expected = discipline;
        assertAll(() -> {
           assertNotNull(firstStudentScore);
           assertEquals(expected, firstStudentScore.getDiscipline());
           assertEquals(students.get(0), firstStudentScore.getStudent());
           assertEquals(50, firstStudentScore.getScore());
        });

        StudentDisciplineScore secondStudentScore = disciplineRepository.getDisciplineScoreByStudentId(discipline.getId(), students.get(1).getId());
        assertAll(() -> {
            assertNotNull(secondStudentScore);
            assertEquals(expected, secondStudentScore.getDiscipline());
            assertEquals(students.get(1), secondStudentScore.getStudent());
            assertEquals(100, secondStudentScore.getScore());
        });
        disciplineRepository.deleteById(discipline.getId());
    }

    @Test
    public void getScoresOfStudents() throws Exception{
        Discipline discipline1 = Discipline.builder()
                .name("Programming I")
                .lecturer(lecturer)
                .assistant(assistant)
                .semester(Semester.FIRST)
                .build();
        disciplineRepository.save(discipline1);
        discipline1 = disciplineRepository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertNotNull(discipline1);

        Discipline discipline2 = Discipline.builder()
                .name("Mathematics I")
                .lecturer(assistant)
                .assistant(lecturer)
                .semester(Semester.SECOND)
                .build();
        disciplineRepository.save(discipline2);
        discipline2 = disciplineRepository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertNotNull(discipline2);

        disciplineService.addGroupToDiscipline(discipline1.getId(), group.getId());
        disciplineService.addGroupToDiscipline(discipline2.getId(), group.getId());
        disciplineRepository.saveStudentScoreToDiscipline(discipline1.getId(), student1.getId(), 10);
        disciplineRepository.saveStudentScoreToDiscipline(discipline2.getId(), student1.getId(), 70);
        List<StudentDisciplineScore> scores = disciplineRepository.getAllScoresOfStudent(student1.getId());
        assertAll(() -> {
            assertNotNull(scores);
            assertEquals(2, scores.size());
            assertEquals(student1, scores.get(0).getStudent());
            assertEquals(student1, scores.get(1).getStudent());
            assertEquals(10, scores.get(0).getScore());
            assertEquals(70, scores.get(1).getScore());
        });
        disciplineRepository.deleteById(discipline1.getId());
        disciplineRepository.deleteById(discipline2.getId());
    }

    @Test
    public void getScoresOfDiscipline() throws Exception{
        Discipline discipline = Discipline.builder()
                .name("Programming I")
                .lecturer(lecturer)
                .assistant(assistant)
                .semester(Semester.FIRST)
                .build();
        disciplineRepository.save(discipline);
        discipline = disciplineRepository.getAll().stream().reduce((first, second) -> second).orElse(null);
        assertNotNull(discipline);

        disciplineService.addGroupToDiscipline(discipline.getId(), group.getId());
        disciplineRepository.saveStudentScoreToDiscipline(discipline.getId(), student1.getId(), 10);
        disciplineRepository.saveStudentScoreToDiscipline(discipline.getId(), student2.getId(), 100);

        List<StudentDisciplineScore> scores = disciplineRepository.getAllScoresOfDiscipline(discipline.getId());
        Discipline expectedDiscipline = discipline;
        assertAll(() -> {
            assertNotNull(scores);
            assertEquals(2, scores.size());
            assertEquals(expectedDiscipline, scores.get(0).getDiscipline());
            assertEquals(expectedDiscipline, scores.get(1).getDiscipline());
            assertEquals(student1, scores.get(0).getStudent());
            assertEquals(student2, scores.get(1).getStudent());
            assertEquals(10, scores.get(0).getScore());
            assertEquals(100, scores.get(1).getScore());
        });
        disciplineRepository.deleteById(discipline.getId());
    }




    private void initializeDepartment() throws Exception {
        connectionBean.initialize();
        department = Department.builder()
                .name("Department1")
                .shortName("dep1")
                .description("Department haha")
                .build();
        departmentRepository.save(department);
        department.setId(departmentRepository.getAll().stream().reduce((first, second) -> second).orElseThrow(Exception::new).getId());
        connectionBean.closeConnection();
    }

    private void initializeGroup() throws Exception{
        connectionBean.initialize();
        group = Group.builder()
                .department(this.department)
                .name("P34131")
                .build();
        groupRepository.save(group);
        group.setId(groupRepository.getAll().stream().reduce((first, second) -> second).orElseThrow(Exception::new).getId());
        connectionBean.closeConnection();
    }

    private void initializeStudents() throws Exception{
        connectionBean.initialize();
        student1 = Student.builder()
                .firstName("Bilguun")
                .lastName("Purevsuren")
                .level(Level.BACHELOR)
                .group(this.group)
                .department(this.department).build();
        student2 = Student.builder()
                .firstName("Bulat")
                .lastName("Khafizov")
                .level(Level.BACHELOR)
                .group(this.group)
                .department(this.department).build();
        studentRepository.save(student1);
        studentRepository.save(student2);
        List<Student> students = studentRepository.getAll();
        List<Student> lastTwo = students.stream().skip(Math.max(0, students.size()-2)).toList();
        student1.setId(lastTwo.get(0).getId());
        student2.setId(lastTwo.get(1).getId());
        connectionBean.closeConnection();
    }

    private void initializeTeacher() throws Exception{
        connectionBean.initialize();
        lecturer = Teacher.builder()
                .firstName("Egor")
                .lastName("Krivonosov")
                .department(this.department)
                .rank(TeacherRank.DISTINGUISHED)
                .build();

        assistant = Teacher.builder()
                .firstName("Sergey")
                .lastName("Klimenkov")
                .department(this.department)
                .rank(TeacherRank.DISTINGUISHED)
                .build();
        teacherRepository.save(lecturer);
        teacherRepository.save(assistant);
        List<Teacher> teachers = teacherRepository.getAll();
        List<Teacher> lastTwo = teachers.stream().skip(Math.max(0, teachers.size()-2)).toList();
        lecturer.setId(lastTwo.get(0).getId());
        assistant.setId(lastTwo.get(1).getId());
        connectionBean.closeConnection();
    }

    @AfterAll
    public void end() throws Exception{
        connectionBean.initialize();
        departmentRepository.deleteById(department.getId());
        groupRepository.deleteById(group.getId());
        studentRepository.deleteById(student1.getId());
        studentRepository.deleteById(student2.getId());
        teacherRepository.deleteById(lecturer.getId());
        teacherRepository.deleteById(assistant.getId());
        connectionBean.closeConnection();
    }
}
