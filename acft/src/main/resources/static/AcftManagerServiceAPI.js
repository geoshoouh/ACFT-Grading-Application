
export async function createNewTestGroup(){
    let groupId = await fetch('http://localhost:8080/testGroup/new', {
        method: 'POST'
      }).then((response) => response.json())
        .catch((error) => {
          console.error('Error: ', error);
        });
    return groupId;
}

export async function createNewSoldier(testGroupId, lastName, firstName, age, isMale){
  let soldierId = await fetch(
    `http://localhost:8080/testGroup/post/${testGroupId}/${lastName}/${firstName}/${age}/${isMale}`,
    {method: 'POST'})
    .then((response) => response.json())
    .catch((error) => {
      console.error('Error: ', error);
    });
  return soldierId;
}

export async function getAllTestGroups(){
  let testGroupIds = await fetch('http://localhost:8080/testGroup/get/all')
    .then((response => response.json()))
    .catch((error) => {
      console.error('Error: ', error);
    });
  return testGroupIds;
}

export async function getEditSoldierDataView(){
  location.replace('http://localhost:8080/editSoldierData');
}

export async function getHomePageView(){
  location.replace('http://localhost:8080');
}

export async function getSoldiersByTestGroupId(testGroupId){
  let response = await fetch(
    `http://localhost:8080/testGroup/getSoldiers/${testGroupId}`
  ).then((response) => response.json())
  .catch((error) => {
    console.error('Error: ', error);
  });
  return response;
}

export async function getSoldierById(soldierId){
  let response = await fetch(
    `http://localhost:8080/soldier/get/${soldierId}`
  ).then((response) => response.json())
  .catch((error) => {
    console.error('Error: ', error);
  });
  return response;
}



