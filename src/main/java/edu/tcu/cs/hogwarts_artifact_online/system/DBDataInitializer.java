package edu.tcu.cs.hogwarts_artifact_online.system;

import edu.tcu.cs.hogwarts_artifact_online.artifact.Artifact;
import edu.tcu.cs.hogwarts_artifact_online.artifact.ArtifactRepository;
import edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.HogwartsUser;
import edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.UserRepository;
import edu.tcu.cs.hogwarts_artifact_online.hogwarts_user.UserService;
import edu.tcu.cs.hogwarts_artifact_online.wizard.Wizard;
import edu.tcu.cs.hogwarts_artifact_online.wizard.WizardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DBDataInitializer implements CommandLineRunner {

    private final ArtifactRepository artifactRepository;

    private final WizardRepository wizardRepository;

    private final UserService userService;

    public DBDataInitializer(ArtifactRepository artifactRepository,
                             WizardRepository wizardRepository,
                             UserService userService) {
        this.artifactRepository = artifactRepository;
        this.wizardRepository = wizardRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        /// Artifact 1 to 5 already saved
        /// automatically by Spring Framework.
        Artifact a1;
        {
            a1 = new Artifact();
            a1.setId("1250808601744904191");
            a1.setName("Deluminator");
            a1.setDescription("A Deluminator is a device invented by Albus Dumbledore that resembles a cigarette lighter. It is used to remove or absorb (as well as return) the light from any light source to provide cover to the user.");
            a1.setImageUrl("ImageUrl");
        }

        Artifact a2;
        {
            a2 = new Artifact();
            a2.setId("1250808601744904192");
            a2.setName("Invisibility Cloak");
            a2.setDescription("An invisibility cloak is used to make the wearer invisible.");
            a2.setImageUrl("ImageUrl");
        }

        Artifact a3;
        {
            a3 = new Artifact();
            a3.setId("1250808601744904193");
            a3.setName("Elder Wand");
            a3.setDescription("The Elder Wand, known throughout history as the Deathstick or the Wand of Destiny, is an extremely powerful wand made of elder wood with a core of Thestral tail hair.");
            a3.setImageUrl("ImageUrl");
        }

        Artifact a4;
        {
            a4 = new Artifact();
            a4.setId("1250808601744904194");
            a4.setName("The Marauder's Map");
            a4.setDescription("A magical map of Hogwarts created by Remus Lupin, Peter Pettigrew, Sirius Black, and James Potter while they were students at Hogwarts.");
            a4.setImageUrl("ImageUrl");
        }

        Artifact a5;
        {
            a5 = new Artifact();
            a5.setId("1250808601744904195");
            a5.setName("The Sword Of Gryffindor");
            a5.setDescription("A goblin-made sword adorned with large rubies on the pommel. It was once owned by Godric Gryffindor, one of the medieval founders of Hogwarts.");
            a5.setImageUrl("ImageUrl");
        }

        Artifact a6;
        {
            a6 = new Artifact();
            a6.setId("1250808601744904196");
            a6.setName("Resurrection Stone");
            a6.setDescription("The Resurrection Stone allows the holder to bring back deceased loved ones, in a semi-physical form, and communicate with them.");
            a6.setImageUrl("ImageUrl");
            artifactRepository.save(a6);
        }

        Wizard w1;
        {
            w1 = new Wizard();
            /// w1.setId(1); /// Commented causing IllegalStateException when adding @GeneratedValue(strategy = Generation.AUTO) in Wizard Entity.
            w1.setName("Albus Dumbledore");
            w1.addArtifact(a1);
            w1.addArtifact(a3);
            wizardRepository.save(w1);
        }

        Wizard w2;
        {
            w2 = new Wizard();
            /// w2.setId(2); /// Commented causing IllegalStateException when adding @GeneratedValue(strategy = Generation.AUTO) in Wizard Entity.
            w2.setName("Harry Potter");
            w2.addArtifact(a2);
            w2.addArtifact(a4);
            wizardRepository.save(w2);
        }

        Wizard w3;
        {
            w3 = new Wizard();
            /// w3.setId(3); /// Commented causing IllegalStateException when adding @GeneratedValue(strategy = Generation.AUTO) in Wizard Entity.
            w3.setName("Neville Longbottom");
            w3.addArtifact(a5);
            wizardRepository.save(w3);
        }

        Wizard w4;
        {
            w4 = new Wizard();
            /// w4.setId(4); /// Commented causing IllegalStateException when adding @GeneratedValue(strategy = Generation.AUTO) in Wizard Entity.
            w4.setName("Agus Dumbledore");
            w4.setArtifacts(null);
            wizardRepository.save(w4);
        }

        HogwartsUser hogwartsUser1 = new HogwartsUser();
        {
            /// hogwartsUser1.setId(index);
            hogwartsUser1.setUsername("Agus");
            hogwartsUser1.setPassword("123456");
            hogwartsUser1.setEnabled(true);
            hogwartsUser1.setRoles("admin user");
            this.userService.save(hogwartsUser1);
        }

        HogwartsUser hogwartsUser2 = new HogwartsUser();
        {
            /// hogwartsUser2.setId(index);
            hogwartsUser2.setUsername("Adang");
            hogwartsUser2.setPassword("654321");
            hogwartsUser2.setEnabled(true);
            hogwartsUser2.setRoles("user");
            this.userService.save(hogwartsUser2);
        }

        HogwartsUser hogwartsUser3 = new HogwartsUser();
        {
            /// hogwartsUser3.setId(index);
            hogwartsUser3.setUsername("Tono");
            hogwartsUser3.setPassword("qwerty");
            hogwartsUser3.setEnabled(false);
            hogwartsUser3.setRoles("user");
            this.userService.save(hogwartsUser3);
        }
    }
}
