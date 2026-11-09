import { useState } from 'react'
import './App.css'
import FuturisticHomePage from './components/home/FuturisticHomePage'

function App() {
  const [count, setCount] = useState(0)

  return (
    <div className="p-10 flex flex-col gap-2 justify-center items-center">
      <FuturisticHomePage />
    </div>
  )
}

export default App
