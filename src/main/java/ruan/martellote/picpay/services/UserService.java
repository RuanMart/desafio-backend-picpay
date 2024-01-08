package ruan.martellote.picpay.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ruan.martellote.picpay.domain.user.User;
import ruan.martellote.picpay.domain.user.UserDTO;
import ruan.martellote.picpay.domain.user.UserType;
import ruan.martellote.picpay.repositories.UserRepository;
import ruan.martellote.picpay.services.exception.BusinessException;
import ruan.martellote.picpay.services.exception.RecordAlreadyExistsException;
import ruan.martellote.picpay.services.exception.RecordNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User createUser(UserDTO userDTO) {
        Optional<User> foundUser = userRepository.findByDocumentAndEmail(userDTO.document(), userDTO.email());
            if (foundUser.isEmpty()) {
                User user = new User(userDTO);
                userRepository.save(user);
                return user;
        } else throw new RecordAlreadyExistsException("CPF ou E-mail já cadastrado");
    }

    public List<User> listUsers() {
        List<User> users = userRepository.findAll();
        return users;
    }

    public User findById(Long id) {
        Optional<User> userObj = userRepository.findById(id);
        return userObj.orElseThrow(() -> new RecordNotFoundException("Usuário não encontrado" + id));
    }

    public void validateTransaction(User payer, BigDecimal value) {
        if (payer.getUserType() == UserType.MERCHANT) throw new BusinessException("Usuário do tipo lojista não está autorizado a realizar transação");
        if (payer.getBalance().compareTo(value) < 0) throw new BusinessException("Saldo insuficiente");
    }

    public void updateUserBalance(User user) {
        userRepository.save(user);
    }
}
