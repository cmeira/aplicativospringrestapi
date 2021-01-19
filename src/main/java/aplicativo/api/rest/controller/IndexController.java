package aplicativo.api.rest.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aplicativo.api.rest.model.Usuario;
import aplicativo.api.rest.repository.TelefoneRepository;
import aplicativo.api.rest.repository.UsuarioRepository;
import aplicativo.api.rest.service.ImplementacaoUserDetailsService;

@CrossOrigin
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	
	@GetMapping(value = "/{id}", produces = "application/json")
	@CachePut("cacheuser")
	public ResponseEntity<Usuario> init(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	

	@GetMapping(value = "/{id}/relatoriopdf", produces = "application/pdf")
	public ResponseEntity<Usuario> relatorio(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		// Retorno seria um relatório.
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}

	@GetMapping(value = "/", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuario() {
		List<Usuario> lista = (List<Usuario>) usuarioRepository.findAll();

		return new ResponseEntity<List<Usuario>>(lista, HttpStatus.OK);
	}
	
	
	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuarioPorNome(@PathVariable("nome") String nome) {
		List<Usuario> lista = (List<Usuario>) usuarioRepository.findByNome(nome);
		return new ResponseEntity<List<Usuario>>(lista, HttpStatus.OK);
	}
		
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody @Valid Usuario usuario) throws Exception {

		for (int i = 0; i < usuario.getListaTelefones().size(); i++) {
			usuario.getListaTelefones().get(i).setUsuario(usuario);
		}

//		URL url = new URL("http://viacep.com.br/ws/" + usuario.getCep() + "/json");
//		URLConnection connection = url.openConnection();
//		InputStream is = connection.getInputStream();
//		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//
//		String cep = "";
//		StringBuilder jsonCEP = new StringBuilder();
//
//		while ((cep = br.readLine()) != null) {
//			jsonCEP.append(cep);
//		}
//
//		Usuario userAux = new Gson().fromJson(jsonCEP.toString(), Usuario.class);
//		usuario.setCep(userAux.getCep());
//		usuario.setLogradouro(userAux.getLogradouro());
//		usuario.setComplemento(userAux.getComplemento());
//		usuario.setBairro(userAux.getBairro());
//		usuario.setLocalidade(userAux.getLocalidade());
//		usuario.setUf(userAux.getUf());

		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
        implementacaoUserDetailsService.insereAcessoPadrao(usuarioSalvo.getId());

		return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);

	}

	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody @Valid Usuario usuario) {

		for (int i = 0; i < usuario.getListaTelefones().size(); i++) {
			usuario.getListaTelefones().get(i).setUsuario(usuario);
		}

		Usuario userTemp = usuarioRepository.findById(usuario.getId()).get();

		if (!userTemp.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}

		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);

	}

	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete(@PathVariable("id") Long id) {
		usuarioRepository.deleteById(id);

		return "ok";
	}
	
	@DeleteMapping(value="/removerTelefone/{id}", produces="application/text")
	public String deletaTelefone(@PathVariable("id") Long id) {
		telefoneRepository.deleteById(id);
		return "ok";
	}
}
