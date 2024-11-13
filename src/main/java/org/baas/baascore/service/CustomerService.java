package org.baas.baascore.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.baas.baascore.excaption.CustomException;
import org.baas.baascore.excaption.ErrorCode;
import org.baas.baascore.model.Customer;
import org.baas.baascore.repository.CustomerRepository;
import org.baas.baascore.util.ResidentNumberEncryptor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final ResidentNumberEncryptor residentNumberEncryptor;

    /**
     * 이름 과 폰번호로 회원 Id를 찾아오는 메서드
     * @param name
     * @param phoneNum
     * @return
     */
    public Long findCustomerByNameAndPhone(String name, String phoneNum) {
        // 이름과 전화번호로 고객을 조회
        Optional<Customer> customerOptional = customerRepository.findByNameAndPhoneNum(name, phoneNum);
        if (customerOptional.isEmpty()) {
            throw new CustomException(ErrorCode.CUSTOMER_NOT_FOUND);
        }
        return customerOptional.get().getId();
    }

    @Transactional
    public void encryptAllResidentNumbers() {
        try {
            List<Customer> members = customerRepository.findAll(); // 모든 회원 가져오기

            for (Customer member : members) {
                String residentNumber = member.getJuminNumber();
                if (residentNumber != null && !residentNumber.isEmpty()) {
                    // 주민번호 암호화
                    String encryptedResidentNumber = residentNumberEncryptor.encrypt(residentNumber);
                    member.setJuminNumber(encryptedResidentNumber);
                }
            }
            customerRepository.saveAll(members); // 변경 사항 저장
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Transactional
    public void decryptAllResidentNumbers() {
        try {
            List<Customer> members = customerRepository.findAll(); // 모든 회원 가져오기

            for (Customer member : members) {
                String residentNumber = member.getJuminNumber();
                if (residentNumber != null && !residentNumber.isEmpty()) {
                    // 주민번호 복호화
                    String encryptedResidentNumber = residentNumberEncryptor.decrypt(residentNumber);
                    member.setJuminNumber(encryptedResidentNumber);
                }
            }
            customerRepository.saveAll(members); // 변경 사항 저장
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
