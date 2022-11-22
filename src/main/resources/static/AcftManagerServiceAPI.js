
export async function createNewTestGroup(passcode = "", host){
  const path = (passcode !== "") ? `${host}/testGroup/new/${passcode}` : `${host}/testGroup/new`;
  let response = await fetch(path, {
        method: 'POST'
      }).then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to ${path} was ${response.status}`);
  return response.json();
}

export async function createNewSoldier(testGroupId, lastName, firstName, age, isMale, passcode = "", host){
  const path = (passcode.length > 0) ? `${host}/testGroup/post/${testGroupId}/${passcode}/${lastName}/${firstName}/${age}/${isMale}` : `${host}/testGroup/post/${testGroupId}/${lastName}/${firstName}/${age}/${isMale}`;
  let response = await fetch(
    path,
    {method: 'POST'})
    .then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to ${path} was ${response.status}`);
  return response.json();
}

export async function getAllTestGroupIds(host){
  let response = await fetch(host + '/testGroup/get/all')
    .then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to /testGroup/get/all was ${response.status}`);
  return response.json();
}

export function getEditSoldierDataView(host){
  location.replace(host + '/editSoldierData');
}

export function getHomePageView(host){
  location.replace(host);
}

export function getAboutView(host){
  location.replace(host + '/about');
}

export async function getSoldiersByTestGroupId(testGroupId, passcode = "", host){
  const path = (passcode !== 0) ? `${host}/testGroup/getSoldiers/${testGroupId}/${passcode}` : `${host}/testGroup/getSoldiers/${testGroupId}`;
  if (typeof testGroupId != "number"){
    console.log(`getSoldiersByTestGroupId in ACFTManagerAPI expected Number; ${typeof soldierId} passed`);
    return;
  }
  let response = await fetch(
    path
  ).then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to ${path} was ${response.status}`);
  return response.json();
}

export async function getSoldierById(soldierId, passcode = "", host){
  const path = (passcode !== "") ? `${host}/soldier/get/${soldierId}/${passcode}` : `${host}/soldier/get/${soldierId}`;
  let response = await fetch(
    path
  ).then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to ${path} was ${response.status}`);
  return response.json();
}

export async function getTestGroupById(testGroupId, passcode = "", host){
  const path = (passcode === "") ? `${host}/testGroup/get/${testGroupId}/` : `${host}/testGroup/get/${testGroupId}/${passcode}`
  let response = await fetch(
    path
  ).then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to ${path} was ${response.status}`);
  return response.json();
}

export async function updateSoldierScore(soldierId, eventId, rawScore, passcode = "", host){
  const path = (passcode === "") ? `${host}/soldier/updateScore/${soldierId}/${eventId-1}/${rawScore}/noPasscode` : `${host}/soldier/updateScore/${soldierId}/${eventId-1}/${rawScore}/${passcode}`;
  let response = await fetch(
    path,
    {method: 'POST'})
    .then((response) => response).catch((error) => console.log(error));
    if (!response.ok) throw Error(`Response to ${path} was ${response.status}`);
    return response.json();
}

export async function downloadTestGroupData(testGroupId, passcode = "", host){
  const path = (passcode === "") ? `${host}/testGroup/getXlsxFile/${testGroupId}` : `${host}/testGroup/getXlsxFile/${testGroupId}/${passcode}`;
  let response = await fetch(
    path
  ).then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to ${path} was ${response.status}`);
  return response.blob();
}

export async function flushDatabase(host){
  const path = host + "/deleteAll";
  let response = await fetch(
    path,
    {method: 'DELETE'}
  ).then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to ${path} was ${response.status}`);
  return response.json();
}

export async function deleteSoldierById(testGroupId, soldierId, passcode = "", host){
  const path = (passcode === "") ? host + `/soldier/delete/${testGroupId}/${soldierId}` : host + `/soldier/delete/${testGroupId}/${soldierId}/${passcode}`;
  let response = await fetch(
    path,
    {method: 'DELETE'}
  ).then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to ${path} was ${response.status}`);
  return response.json();
}

export async function populateDatabase(size, host){
  const path = host + `/populateDatabase/${size}`;
  let response = await fetch(
    path,
    {method: 'POST'}
  ).then((response) => response).catch((error) => console.log(error));
  if (!response.ok) throw Error(`Response to ${path} was ${response.status}`);
  return response.json();
}
