package com.licentamihai.alumni.repository;

import com.licentamihai.alumni.model.SimpleUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimpleUserRepository extends MongoRepository<SimpleUser, String> {

    public SimpleUser findByUsernameAndPassword(String username, String password);

    public SimpleUser findByUsername(String username);

    public SimpleUser deleteByUsername(String username);

    // TODO: THIS QUERY below doesn't work because groups is just a field called dbref, not the actual groups
    // i should check this hyposthesis
    //  Intrebarea de pe stack are un specific ciudat... in baza de date nu ar trebui sa fie nested docs, ci dbref care arata catre alte docs.
    //deci in cazul meu eu cred ca query -ul nu merge pentru ca el cauta dupa elementul nested, dar care e incarcat la nivel de aplicatie,
    //iar query ul e la nivel de baza de date si nu vede doc nested.
    //eu ar trebui sa incarca toate docs si apoi sa le sortez cu java streams.
    // UPDATE: asa am facut dar as vrea totusi sa vad daca se poate mai eficient, gen sa nu incarc chiar tot , poate chiar sa fac o lista de users
    // in groups
//    @Query(value="{ 'groups': { $elemMatch: { 'name' : ?0 } }}")
//    List<SimpleUser> findByGroupName(String groupName);
}
