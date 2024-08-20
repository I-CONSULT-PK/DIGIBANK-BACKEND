    package com.example.AddPayeeService.controller;

    import com.example.AddPayeeService.model.dto.BanksDto;
    import com.example.AddPayeeService.model.dto.CbsAccountDto;
    import com.example.AddPayeeService.model.dto.request.AddPayeeRequestDto;
    import com.example.AddPayeeService.model.dto.response.AddPayeeResponseDto;
    import com.example.AddPayeeService.service.AddPayeeService;
    import com.zanbeel.customUtility.model.CustomResponseEntity;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;


    @RestController
    @RequestMapping("/v1/beneficiary")

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

        @GetMapping("/getAllBeneficiary")
        public CustomResponseEntity getAllBeneficiary(@RequestParam Long customerId, @RequestParam Boolean flag) throws Exception {
            return addPayeeService.getAllBeneficiaries(customerId ,flag);

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

        @PostMapping("/addToFavourite")
        public CustomResponseEntity addToFavouriteBene (@RequestParam  Long beneId , @RequestParam boolean flag , @RequestParam Long customerId){
            return addPayeeService.addToFavourite(beneId, flag , customerId);
        }


    }
