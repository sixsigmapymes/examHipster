package com.acero.ocho.web.rest;

import com.acero.ocho.A08App;
import com.acero.ocho.domain.Persona;
import com.acero.ocho.repository.PersonaRepository;
import com.acero.ocho.service.PersonaService;
import com.acero.ocho.service.dto.PersonaDTO;
import com.acero.ocho.service.mapper.PersonaMapper;
import com.acero.ocho.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.acero.ocho.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.acero.ocho.domain.enumeration.Sexo;
/**
 * Integration tests for the {@Link PersonaResource} REST controller.
 */
@SpringBootTest(classes = A08App.class)
public class PersonaResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_APELLIDO = "AAAAAAAAAA";
    private static final String UPDATED_APELLIDO = "BBBBBBBBBB";

    private static final Long DEFAULT_EDAD = 1L;
    private static final Long UPDATED_EDAD = 2L;

    private static final Sexo DEFAULT_SEXO = Sexo.HOMBRE;
    private static final Sexo UPDATED_SEXO = Sexo.MUJER;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PersonaMapper personaMapper;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restPersonaMockMvc;

    private Persona persona;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PersonaResource personaResource = new PersonaResource(personaService);
        this.restPersonaMockMvc = MockMvcBuilders.standaloneSetup(personaResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Persona createEntity(EntityManager em) {
        Persona persona = new Persona()
            .nombre(DEFAULT_NOMBRE)
            .apellido(DEFAULT_APELLIDO)
            .edad(DEFAULT_EDAD)
            .sexo(DEFAULT_SEXO);
        return persona;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Persona createUpdatedEntity(EntityManager em) {
        Persona persona = new Persona()
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .edad(UPDATED_EDAD)
            .sexo(UPDATED_SEXO);
        return persona;
    }

    @BeforeEach
    public void initTest() {
        persona = createEntity(em);
    }

    @Test
    @Transactional
    public void createPersona() throws Exception {
        int databaseSizeBeforeCreate = personaRepository.findAll().size();

        // Create the Persona
        PersonaDTO personaDTO = personaMapper.toDto(persona);
        restPersonaMockMvc.perform(post("/api/personas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(personaDTO)))
            .andExpect(status().isCreated());

        // Validate the Persona in the database
        List<Persona> personaList = personaRepository.findAll();
        assertThat(personaList).hasSize(databaseSizeBeforeCreate + 1);
        Persona testPersona = personaList.get(personaList.size() - 1);
        assertThat(testPersona.getNombre()).isEqualTo(DEFAULT_NOMBRE);
        assertThat(testPersona.getApellido()).isEqualTo(DEFAULT_APELLIDO);
        assertThat(testPersona.getEdad()).isEqualTo(DEFAULT_EDAD);
        assertThat(testPersona.getSexo()).isEqualTo(DEFAULT_SEXO);
    }

    @Test
    @Transactional
    public void createPersonaWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = personaRepository.findAll().size();

        // Create the Persona with an existing ID
        persona.setId(1L);
        PersonaDTO personaDTO = personaMapper.toDto(persona);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPersonaMockMvc.perform(post("/api/personas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(personaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Persona in the database
        List<Persona> personaList = personaRepository.findAll();
        assertThat(personaList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllPersonas() throws Exception {
        // Initialize the database
        personaRepository.saveAndFlush(persona);

        // Get all the personaList
        restPersonaMockMvc.perform(get("/api/personas?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(persona.getId().intValue())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE.toString())))
            .andExpect(jsonPath("$.[*].apellido").value(hasItem(DEFAULT_APELLIDO.toString())))
            .andExpect(jsonPath("$.[*].edad").value(hasItem(DEFAULT_EDAD.intValue())))
            .andExpect(jsonPath("$.[*].sexo").value(hasItem(DEFAULT_SEXO.toString())));
    }
    
    @Test
    @Transactional
    public void getPersona() throws Exception {
        // Initialize the database
        personaRepository.saveAndFlush(persona);

        // Get the persona
        restPersonaMockMvc.perform(get("/api/personas/{id}", persona.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(persona.getId().intValue()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE.toString()))
            .andExpect(jsonPath("$.apellido").value(DEFAULT_APELLIDO.toString()))
            .andExpect(jsonPath("$.edad").value(DEFAULT_EDAD.intValue()))
            .andExpect(jsonPath("$.sexo").value(DEFAULT_SEXO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPersona() throws Exception {
        // Get the persona
        restPersonaMockMvc.perform(get("/api/personas/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePersona() throws Exception {
        // Initialize the database
        personaRepository.saveAndFlush(persona);

        int databaseSizeBeforeUpdate = personaRepository.findAll().size();

        // Update the persona
        Persona updatedPersona = personaRepository.findById(persona.getId()).get();
        // Disconnect from session so that the updates on updatedPersona are not directly saved in db
        em.detach(updatedPersona);
        updatedPersona
            .nombre(UPDATED_NOMBRE)
            .apellido(UPDATED_APELLIDO)
            .edad(UPDATED_EDAD)
            .sexo(UPDATED_SEXO);
        PersonaDTO personaDTO = personaMapper.toDto(updatedPersona);

        restPersonaMockMvc.perform(put("/api/personas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(personaDTO)))
            .andExpect(status().isOk());

        // Validate the Persona in the database
        List<Persona> personaList = personaRepository.findAll();
        assertThat(personaList).hasSize(databaseSizeBeforeUpdate);
        Persona testPersona = personaList.get(personaList.size() - 1);
        assertThat(testPersona.getNombre()).isEqualTo(UPDATED_NOMBRE);
        assertThat(testPersona.getApellido()).isEqualTo(UPDATED_APELLIDO);
        assertThat(testPersona.getEdad()).isEqualTo(UPDATED_EDAD);
        assertThat(testPersona.getSexo()).isEqualTo(UPDATED_SEXO);
    }

    @Test
    @Transactional
    public void updateNonExistingPersona() throws Exception {
        int databaseSizeBeforeUpdate = personaRepository.findAll().size();

        // Create the Persona
        PersonaDTO personaDTO = personaMapper.toDto(persona);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonaMockMvc.perform(put("/api/personas")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(personaDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Persona in the database
        List<Persona> personaList = personaRepository.findAll();
        assertThat(personaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePersona() throws Exception {
        // Initialize the database
        personaRepository.saveAndFlush(persona);

        int databaseSizeBeforeDelete = personaRepository.findAll().size();

        // Delete the persona
        restPersonaMockMvc.perform(delete("/api/personas/{id}", persona.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Persona> personaList = personaRepository.findAll();
        assertThat(personaList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Persona.class);
        Persona persona1 = new Persona();
        persona1.setId(1L);
        Persona persona2 = new Persona();
        persona2.setId(persona1.getId());
        assertThat(persona1).isEqualTo(persona2);
        persona2.setId(2L);
        assertThat(persona1).isNotEqualTo(persona2);
        persona1.setId(null);
        assertThat(persona1).isNotEqualTo(persona2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PersonaDTO.class);
        PersonaDTO personaDTO1 = new PersonaDTO();
        personaDTO1.setId(1L);
        PersonaDTO personaDTO2 = new PersonaDTO();
        assertThat(personaDTO1).isNotEqualTo(personaDTO2);
        personaDTO2.setId(personaDTO1.getId());
        assertThat(personaDTO1).isEqualTo(personaDTO2);
        personaDTO2.setId(2L);
        assertThat(personaDTO1).isNotEqualTo(personaDTO2);
        personaDTO1.setId(null);
        assertThat(personaDTO1).isNotEqualTo(personaDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(personaMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(personaMapper.fromId(null)).isNull();
    }
}
