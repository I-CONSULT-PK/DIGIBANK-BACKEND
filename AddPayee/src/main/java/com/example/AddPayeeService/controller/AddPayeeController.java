    package com.example.AddPayeeService.controller;

    import com.example.AddPayeeService.model.dto.BanksDto;
    import com.example.AddPayeeService.model.dto.CbsAccountDto;
    import com.example.AddPayeeService.model.dto.request.AddPayeeRequestDto;
    import com.example.AddPayeeService.model.dto.response.AddPayeeResponseDto;
    import com.example.AddPayeeService.model.entity.AddPayee;
    import com.example.AddPayeeService.service.AddPayeeService;
    import com.zanbeel.customUtility.model.CustomResponseEntity;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.http.ResponseEntity;

    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;


    @RestController
    @RequestMapping("/beneficiary")

    public class AddPayeeController  {

        @Autowired
        private AddPayeeService addPayeeService;

        //Create
        @PostMapping("/createBeneficiary")
        public CustomResponseEntity saveBeneficiary (@RequestBody AddPayeeRequestDto addPayee) throws Exception {

            return addPayeeService.createBeneficiary(addPayee);

        }

        @PostMapping("/updateBeneficiary")
        public CustomResponseEntity updateBeneficiary (@RequestBody AddPayeeRequestDto addPayee) throws Exception {

            return addPayeeService.updateBene(addPayee);

        }

        @GetMapping("/getBanks")
        public CustomResponseEntity<List<BanksDto>> getBenksList(){
          return addPayeeService.getAllBanks();

        }

        @GetMapping("/getAccount")
        public CustomResponseEntity<CbsAccountDto> getAccount(@RequestParam String accountNumber, @RequestParam String bankName){
            return addPayeeService.getAccountDetails(accountNumber, bankName);

        }

        @GetMapping("/getAllBeneficiary/{customerId}")
        public List<CustomResponseEntity<AddPayeeResponseDto>> getAllBeneficiary(@PathVariable Long customerId) throws Exception {
            List<CustomResponseEntity<AddPayeeResponseDto>> payees = addPayeeService.getAllBeneficiaries(customerId);
            return payees;
        }

        @GetMapping("/getBeneficiary/{beneId}")
        public CustomResponseEntity<AddPayeeResponseDto> getBeneficiaryById(@PathVariable Long beneId) throws Exception {
            CustomResponseEntity<AddPayeeResponseDto> payees = addPayeeService.getAddPayee(beneId);
            return payees;
        }

        @PostMapping("/deleteBene/{beneId}")
            public CustomResponseEntity deleteBene (@PathVariable Long beneId){
            return addPayeeService.deleteBene(beneId);
            }
        //get single user
//        @GetMapping("/{beneficiaryId}")
//        public ResponseEntity<AddPayee> getSingleBeneficiary (@PathVariable String beneficiaryId){
//            AddPayee payee = addPayeeService.getAddPayee(beneficiaryId);
//            return ResponseEntity.ok(payee);
//        }

        //get all User
//        @GetMapping
//        public ResponseEntity<List<AddPayee>> getAllBeneficiary(){
//            List<AddPayee> allBeneficiary = addPayeeService.getAllBeneficiaries();
//            return ResponseEntity.ok(allBeneficiary);
//
//        }

        @GetMapping("/getLocalAccountTitle")
        public CustomResponseEntity getLocalAccountTitle(@RequestParam("senderAccountNumber") String senderAccountNumber) {
            return this.addPayeeService.getLocalAccountTitle(senderAccountNumber);
        }


    }
