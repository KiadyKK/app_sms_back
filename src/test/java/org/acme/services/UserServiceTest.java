package org.acme.services;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.build.Jwt;
import io.vertx.core.json.JsonObject;
import jakarta.ws.rs.core.Response;
import org.acme.model.app_sms_833.User;
import org.acme.repo.app_sms_833.UserRepo;
import org.acme.requests.AddUserReq;
import org.acme.requests.LoginReq;
import org.acme.requests.PutPasswordReq;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import jakarta.inject.Inject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@QuarkusTest
class UserServiceTest {
    @Inject
    UserService userService;
    @InjectMock
    UserRepo userRepo;
    private static  List<User> userList;
    @ConfigProperty(name = "admin.tri")
    private String ADMIN_TRI;
    @ConfigProperty(name = "admin.mdp")
    private String ADMIN_MDP;
    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        User user=Mockito.mock(User.class);
        User user1=Mockito.mock(User.class);
        Mockito.when(user.getTri()).thenReturn("iol");
        Mockito.when(user.getTel()).thenReturn("0345415027");
        Mockito.when(user.getStatus()).thenReturn(1);
        Mockito.when(user.getRole()).thenReturn(1);
        Mockito.when(user.getPrenom()).thenReturn("John");
        Mockito.when(user.getNom()).thenReturn("Doe");
        Mockito.when(user.getMdp()).thenReturn(BCrypt.hashpw("iol",BCrypt.gensalt()));
        Mockito.when(user.getEmail()).thenReturn("john@gmail.com");

        Mockito.when(user1.getTri()).thenReturn("dny");
        Mockito.when(user1.getTel()).thenReturn("0329966415");
        Mockito.when(user1.getStatus()).thenReturn(1);
        Mockito.when(user1.getRole()).thenReturn(1);
        Mockito.when(user1.getPrenom()).thenReturn("Jane");
        Mockito.when(user1.getNom()).thenReturn("Diane");
        Mockito.when(user1.getMdp()).thenReturn(BCrypt.hashpw("dny",BCrypt.gensalt()));
        Mockito.when(user1.getEmail()).thenReturn("jane@gmail.com");

        userList= Arrays.asList(user,user1);
    }
    @Test
    void loginSuccess() {
        LoginReq req=Mockito.mock(LoginReq.class);
        Mockito.when(req.getTri()).thenReturn("iol");
        Mockito.when(req.getMdp()).thenReturn("iol");

        User user=Mockito.mock(User.class);
        Mockito.when(user.getTri()).thenReturn("iol");
        Mockito.when(user.getMdp()).thenReturn(BCrypt.hashpw("iol",BCrypt.gensalt()));
        Mockito.when(userRepo.findByTri("iol")).thenReturn(user);
        Response response=userService.login(req);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());

        Mockito.verify(userRepo).findByTri(any(String.class));
    }
    @Test
    void loginWrongPassword(){
        LoginReq req=Mockito.mock(LoginReq.class);
        Mockito.when(req.getTri()).thenReturn("iol");
        Mockito.when(req.getMdp()).thenReturn("wrongPassword");

        User user=Mockito.mock(User.class);
        Mockito.when(user.getTri()).thenReturn("iol");
        Mockito.when(user.getMdp()).thenReturn(BCrypt.hashpw("iol",BCrypt.gensalt()));
        Mockito.when(userRepo.findByTri("iol")).thenReturn(user);
        Response response=userService.login(req);
        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(),response.getStatus());

        Mockito.verify(userRepo).findByTri(any(String.class));
    }
    @Test
    void loginUserNotFound(){
        LoginReq req=Mockito.mock(LoginReq.class);
        Mockito.when(req.getTri()).thenReturn("iol");
        Mockito.when(req.getMdp()).thenReturn("iol");

        Mockito.when(userRepo.findByTri(any(String.class))).thenReturn(null);
        Response response=userService.login(req);
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),response.getStatus());

        Mockito.verify(userRepo).findByTri(any(String.class));
    }
    @Test
    void loginAdmin(){
        LoginReq req=Mockito.mock(LoginReq.class);
        User user=Mockito.mock(User.class);
        Mockito.when(req.getTri()).thenReturn(ADMIN_TRI);
        Mockito.when(req.getMdp()).thenReturn(ADMIN_MDP);

        Mockito.when(userRepo.findByTri(any(String.class))).thenReturn(user);

        Response response=userService.login(req);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
    }

    @Test
    void modifyMdpSuccess(){
        PutPasswordReq req=Mockito.mock(PutPasswordReq.class);
        Mockito.when(req.getTrigramme()).thenReturn("iol");
        Mockito.when(req.getPassword()).thenReturn("iol");
        Mockito.when(req.getNewPassword()).thenReturn("1234");
        User user=Mockito.mock(User.class);
        Mockito.when(user.getTri()).thenReturn("iol");
        Mockito.when(user.getMdp()).thenReturn(BCrypt.hashpw("iol",BCrypt.gensalt()));
        Mockito.when(userRepo.findByTri(req.getTrigramme())).thenReturn(user);
        boolean response=userService.modifyMdp(req);
        assertTrue(response);
        Mockito.verify(userRepo).findByTri(any(String.class));
        Mockito.verify(user).setMdp(anyString());

    }
    @Test
    void modifyMdpFailureWrongPassword(){
        PutPasswordReq req=Mockito.mock(PutPasswordReq.class);
        Mockito.when(req.getTrigramme()).thenReturn("iol");
        Mockito.when(req.getPassword()).thenReturn("ioliol");
        Mockito.when(req.getNewPassword()).thenReturn("1234");

        User user=Mockito.mock(User.class);
        Mockito.when(user.getTri()).thenReturn("iol");
        Mockito.when(user.getMdp()).thenReturn(BCrypt.hashpw("iol",BCrypt.gensalt()));

        Mockito.when(userRepo.findByTri(req.getTrigramme())).thenReturn(user);
        boolean response=userService.modifyMdp(req);
        assertFalse(response);
        Mockito.verify(userRepo).findByTri(any(String.class));
        verify(user, never()).setMdp(anyString());
    }
    @Test
    void modifyMdpUserNotFound() {
        PutPasswordReq req = Mockito.mock(PutPasswordReq.class);
        Mockito.when(req.getTrigramme()).thenReturn("iol");
        Mockito.when(req.getPassword()).thenReturn("iol");
        Mockito.when(req.getNewPassword()).thenReturn("1234");

        Mockito.when(userRepo.findByTri(req.getTrigramme())).thenReturn(null);

        boolean response = userService.modifyMdp(req);

        assertFalse(response);
        Mockito.verify(userRepo).findByTri(anyString());
    }
    @Test
    void generateJwt() throws Exception{
      User user= Mockito.mock(User.class);
        Mockito.when(user.getTri()).thenReturn("iol");
        Mockito.when(user.getPrenom()).thenReturn("John");
        Mockito.when(user.getNom()).thenReturn("Doe");

        Response response=userService.callerMethod(user);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());

        JsonObject jsonObject = (JsonObject) response.getEntity();
        assertNotNull(jsonObject.getString("token"));
        assertTrue(jsonObject.getString("token").startsWith("Bearer "));
        User returnedUser = (User) jsonObject.getValue("user");
        assertEquals(user, returnedUser);
    }
    @Test
    void addUser() {
        AddUserReq addUserReq=Mockito.mock(AddUserReq.class);
        Mockito.when(addUserReq.getTri()).thenReturn("iol");
        Mockito.when(addUserReq.getTel()).thenReturn("0345415027");
        Mockito.when(addUserReq.getPrenom()).thenReturn("John");
        Mockito.when(addUserReq.getNom()).thenReturn("Doe");
        Mockito.when(addUserReq.getEmail()).thenReturn("john@gmail.com");

        User user=new User(addUserReq);

        doNothing().when(userRepo).persist(user);

        Response response=userService.addUser(addUserReq);
        User entity=(User) response.getEntity();

        assertNotNull(response);
        assertNotNull(entity);
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());
        assertEquals("iol",entity.getTri());

        Mockito.verify(userRepo).persist(any(User.class));
    }
    @Test
    void addUserWithException () throws Exception{
        AddUserReq addUserReq=Mockito.mock(AddUserReq.class);
        Mockito.when(addUserReq.getTri()).thenReturn("iol");
        Mockito.when(addUserReq.getTel()).thenReturn("0345415027");
        Mockito.when(addUserReq.getPrenom()).thenReturn("John");
        Mockito.when(addUserReq.getNom()).thenReturn("Doe");
        Mockito.when(addUserReq.getEmail()).thenReturn("john@gmail.com");

        doThrow(new RuntimeException("Database error")).when(userRepo).persist(any(User.class));
        Response response =userService.addUser(addUserReq);
        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());

        Mockito.verify(userRepo).persist(any(User.class));
    }

    @Test
    void getAll() {
        Mockito.when(userRepo.getAll("")).thenReturn(userList);

        Response response=userService.getAll("");
        List<User> entity=(List<User>)response.getEntity();

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals(2,entity.size());
        assertEquals(userList.get(0),entity.get(0));
        assertEquals(userList.get(1),entity.get(1));
        assertEquals(Response.Status.OK.getStatusCode(),response.getStatus());

        Mockito.verify(userRepo).getAll(any(String.class));

    }
    @Test
    void getAllWithException() throws Exception{
        Mockito.when(userRepo.getAll("")).thenThrow(new RuntimeException("Database error"));

        Response response=userService.getAll("");

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());

        Mockito.verify(userRepo).getAll(any(String.class));
    }

    @Test
    void delete() {
        long id=1L;
        Mockito.when(userRepo.remove(id)).thenReturn(any(Integer.class));

        Response response=userService.delete(id);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(),response.getStatus());

        Mockito.verify(userRepo).remove(any(Long.class));
    }
    @Test
    void deleteWithException(){
        long id=1L;
        doThrow(new RuntimeException()).when(userRepo).remove(id);

        Response response=userService.delete(id);

        assertNotNull(response);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),response.getStatus());

        Mockito.verify(userRepo).remove(any(Long.class));
    }

}