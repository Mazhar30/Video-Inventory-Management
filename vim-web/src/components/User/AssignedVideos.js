import React, { useEffect, useState } from 'react';
import { fetchVideos, updateVideo } from '../../api/api';
import { Link } from 'react-router-dom';
import ReactPaginate from 'react-paginate';
import '../../static/css/AssignedVideos.css';

const AssignedVideos = () => {
    const [videos, setVideos] = useState([]);
    const [editingVideoId, setEditingVideoId] = useState(null);
    const [description, setDescription] = useState("");
    const [pageCount, setPageCount] = useState(0);
    const [currentPage, setCurrentPage] = useState(0);

    const videosPerPage = 5;

    useEffect(() => {
        const loadAssignedVideos = async () => {
            try {
                const fetchedVideos = await fetchVideos();
                setVideos(fetchedVideos);
                setPageCount(Math.ceil(fetchedVideos.length / videosPerPage));
            } catch (error) {
                console.error('Error loading videos:', error);
            }
        };
        loadAssignedVideos();
    }, []);

    const handlePageChange = ({ selected }) => {
        setCurrentPage(selected);
    };

    const indexOfLastVideo = (currentPage + 1) * videosPerPage;
    const indexOfFirstVideo = indexOfLastVideo - videosPerPage;
    const currentVideos = videos.slice(indexOfFirstVideo, indexOfLastVideo);

    const handleEditClick = (video) => {
        setEditingVideoId(video.id);
        setDescription(video.description);
    };

    const handleSaveClick = async (videoId) => {
        try {
            const videoData = { id: videoId, description, title: null };
            await updateVideo(videoData);
            setVideos((prevVideos) =>
                prevVideos.map((video) =>
                    video.id === videoId ? { ...video, description } : video
                )
            );
            setEditingVideoId(null);
        } catch (error) {
            console.error('Error updating description:', error);
        }
    };

    const handleCancelClick = () => {
        setEditingVideoId(null);
        setDescription('');
    };

    return (
        <div className="assigned-videos-container">
            <h2>Your Assigned Videos</h2>

            {videos.length === 0 ? (
                <p>No videos assigned</p>
            ) : (
                <>
                    <table className="assigned-videos-table">
                        <thead>
                            <tr>
                                <th>Title</th>
                                <th>Description</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {currentVideos.map((video) => (
                                <tr key={video.id}>
                                    <td>
                                        <Link to={`/user/video/${video.id}`}>{video.title}</Link>
                                    </td>
                                    <td>
                                        {editingVideoId === video.id ? (
                                            <input
                                                type="text"
                                                value={description}
                                                onChange={(e) => setDescription(e.target.value)}
                                            />
                                        ) : (
                                            video.description
                                        )}
                                    </td>
                                    <td>
                                        {editingVideoId === video.id ? (
                                            <>
                                                <button onClick={() => handleSaveClick(video.id)}>
                                                    Save
                                                </button>
                                                <button onClick={handleCancelClick}>
                                                    Cancel
                                                </button>
                                            </>
                                        ) : (
                                            <button onClick={() => handleEditClick(video)}>
                                                Edit
                                            </button>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    <ReactPaginate
                        previousLabel={"Previous"}
                        nextLabel={"Next"}
                        pageCount={pageCount}
                        onPageChange={handlePageChange}
                        containerClassName={"pagination"}
                        pageClassName={"page-item"}
                        pageLinkClassName={"page-link"}
                        previousClassName={"previous-item"}
                        nextClassName={"next-item"}
                        activeClassName={"active"}
                    />
                </>
            )}
        </div>
    );
};

export default AssignedVideos;